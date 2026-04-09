#!/bin/bash

#
# Generates the FIPS 140-3 software integrity manifest.
#
# Run this script after building portal-kernel and portal-impl to produce
# the SHA-256 digests that CryptoStartupAction verifies at startup.
#
# Usage:
#   ./generate-fips-integrity.sh [classes-dir...]
#
# If no arguments are given, the script searches the default build output
# directories for the critical class files.
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

MANIFEST="${SCRIPT_DIR}/portal-impl/src/META-INF/fips-integrity.properties"

CRITICAL_CLASSES=(
	"com/liferay/portal/kernel/security/fips/FIPSModeUtil.class"
	"com/liferay/portal/kernel/security/fips/CompanyKeyStoreUtil.class"
	"com/liferay/portal/kernel/util/DigesterUtil.class"
)

# Default search paths for compiled classes
if [ $# -gt 0 ]; then
	SEARCH_DIRS=("$@")
else
	SEARCH_DIRS=(
		"${SCRIPT_DIR}/portal-kernel/classes"
		"${SCRIPT_DIR}/portal-impl/classes"
		"${SCRIPT_DIR}/lib/portal"
	)
fi

compute_digest() {
	local class_path="$1"

	for search_dir in "${SEARCH_DIRS[@]}"; do
		local full_path="${search_dir}/${class_path}"

		if [ -f "${full_path}" ]; then
			sha256sum "${full_path}" | awk '{print $1}'
			return 0
		fi
	done

	# Try finding in JAR files
	for search_dir in "${SEARCH_DIRS[@]}"; do
		if [ -d "${search_dir}" ]; then
			for jar in "${search_dir}"/*.jar; do
				if [ -f "${jar}" ] && unzip -l "${jar}" "${class_path}" >/dev/null 2>&1; then
					local tmpdir
					tmpdir=$(mktemp -d)
					unzip -q -o "${jar}" "${class_path}" -d "${tmpdir}" 2>/dev/null
					if [ -f "${tmpdir}/${class_path}" ]; then
						sha256sum "${tmpdir}/${class_path}" | awk '{print $1}'
						rm -rf "${tmpdir}"
						return 0
					fi
					rm -rf "${tmpdir}"
				fi
			done
		fi
	done

	return 1
}

echo "Generating FIPS integrity manifest: ${MANIFEST}"

cat > "${MANIFEST}" << 'HEADER'
#
# FIPS 140-3 Software Integrity Manifest (SP 800-140B Section 10.2.1.1)
#
# This file contains SHA-256 digests of critical cryptographic classes.
# It is verified at startup when FIPS mode is enabled.
#
# Regenerate after every build with:
#   ./generate-fips-integrity.sh
#
# Format: class/path.class=hex-encoded-sha256-digest
#
HEADER

errors=0

for class_path in "${CRITICAL_CLASSES[@]}"; do
	digest=$(compute_digest "${class_path}" 2>/dev/null) || true

	if [ -z "${digest}" ]; then
		echo "ERROR: could not find ${class_path} in search paths"
		errors=$((errors + 1))
		continue
	fi

	echo "${class_path}=${digest}" >> "${MANIFEST}"
	echo "  ${class_path} = ${digest}"
done

if [ ${errors} -gt 0 ]; then
	echo ""
	echo "WARNING: ${errors} class(es) not found. Searched in:"
	for search_dir in "${SEARCH_DIRS[@]}"; do
		echo "  ${search_dir}"
	done
	echo ""
	echo "Pass the classes directory as an argument if using a non-default build path:"
	echo "  ./generate-fips-integrity.sh /path/to/classes"
	exit 1
fi

echo ""
echo "Manifest written to ${MANIFEST}"
