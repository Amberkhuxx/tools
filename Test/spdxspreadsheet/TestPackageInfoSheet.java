/**
 * Copyright (c) 2011 Source Auditor Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package spdxspreadsheet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spdx.rdfparser.SPDXPackageInfo;
import org.spdx.rdfparser.SpdxPackageVerificationCode;
import org.spdx.rdfparser.license.AnyLicenseInfo;
import org.spdx.rdfparser.license.ConjunctiveLicenseSet;
import org.spdx.rdfparser.license.DisjunctiveLicenseSet;
import org.spdx.rdfparser.license.ExtractedLicenseInfo;
import org.spdx.rdfparser.license.SpdxNoneLicense;
import org.spdx.spdxspreadsheet.OriginsSheet;
import org.spdx.spdxspreadsheet.PackageInfoSheet;
import org.spdx.spdxspreadsheet.PackageInfoSheetV09d3;
import org.spdx.spdxspreadsheet.SPDXSpreadsheet;
import org.spdx.spdxspreadsheet.SpreadsheetException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

public class TestPackageInfoSheet {
	
	File spreadsheetFile;

	@Before
	public void setUp() throws Exception {
		spreadsheetFile = File.createTempFile("TEST_PKG_INFO", "xls");
	}

	@After
	public void tearDown() throws Exception {
		spreadsheetFile.delete();
	}


	@Test
	public void testCreate() throws IOException, InvalidFormatException {
		
		Workbook wb = new HSSFWorkbook();
		PackageInfoSheet.create(wb, "Package Info");
		PackageInfoSheet pkgInfo = PackageInfoSheet.openVersion(wb, "Package Info", SPDXSpreadsheet.CURRENT_VERSION);
		String ver = pkgInfo.verify();
		if (ver != null && !ver.isEmpty()){
			fail(ver);
		}
	}

	@Test
	public void testAddAndGet() throws SpreadsheetException {
		AnyLicenseInfo[] testLicenses1 = new AnyLicenseInfo[3];
		testLicenses1[0] = new ExtractedLicenseInfo("License1", "License1Text");
		AnyLicenseInfo[] disjunctiveLicenses = new AnyLicenseInfo[3];
		disjunctiveLicenses[0] = new ExtractedLicenseInfo("disj1", "disj1 Text");
		disjunctiveLicenses[1] = new ExtractedLicenseInfo("disj2", "disj2 Text");
		disjunctiveLicenses[2] = new ExtractedLicenseInfo("disj3", "disj3 Text");
		testLicenses1[1] = new DisjunctiveLicenseSet(disjunctiveLicenses);
		AnyLicenseInfo[] conjunctiveLicenses = new AnyLicenseInfo[] {
				new ExtractedLicenseInfo("conj1", "conj1 Text"),
				new ExtractedLicenseInfo("conj2", "conj2 Text")
		};
		testLicenses1[2] = new ConjunctiveLicenseSet(conjunctiveLicenses);
		AnyLicenseInfo testLicense1 = new DisjunctiveLicenseSet(testLicenses1);
		
//		String lic1String = PackageInfoSheet.licensesToString(testLicenses1);
		AnyLicenseInfo[] testLicenses2 = new AnyLicenseInfo[2];
		testLicenses2[0] = new ExtractedLicenseInfo("License3", "License 3 text");
		testLicenses2[1] = new ExtractedLicenseInfo("License4", "License 4 text");
		AnyLicenseInfo testLicense2 = new ConjunctiveLicenseSet(testLicenses2);
		AnyLicenseInfo[] testLicenseInfos = new AnyLicenseInfo[] {new SpdxNoneLicense()};
		SpdxPackageVerificationCode testVerification = new SpdxPackageVerificationCode("value",
				new String[] {"skippedfil1", "skippedfile2"});
//		String lic2String = PackageInfoSheet.licensesToString(testLicenses2);
		SPDXPackageInfo pkgInfo1 = new SPDXPackageInfo("decname1", "Version1", "machinename1", 
				"sha1-1", "sourceinfo1", testLicense1,
				testLicense2, testLicenseInfos, "license comments", "dec-copyright1",
				"short desc1", "desc1", "http://url1", testVerification, "Person: supplier1", "Organization: originator1",
				"http://www.home.page1");
		SPDXPackageInfo pkgInfo2 = new SPDXPackageInfo("decname1", "Version2", "machinename1", 
				"sha1-1", "sourceinfo1", testLicense1,
				testLicense2, testLicenseInfos, "licensecomments2", "dec-copyright1",
				"short desc1", "desc1", "http://url1", testVerification, "NOASSERTION", "Person: originator2",
				"http://www.home.page2");
		Workbook wb = new HSSFWorkbook();
		PackageInfoSheet.create(wb, "Package Info");
		PackageInfoSheet pkgInfoSheet = PackageInfoSheet.openVersion(wb, "Package Info", SPDXSpreadsheet.CURRENT_VERSION);
		pkgInfoSheet.add(pkgInfo1);
		pkgInfoSheet.add(pkgInfo2);
		SPDXPackageInfo tstPkgInfo1 = pkgInfoSheet.getPackageInfo(1);
		SPDXPackageInfo tstPkgInfo2 = pkgInfoSheet.getPackageInfo(2);
		comparePkgInfo(pkgInfo1, tstPkgInfo1);
		comparePkgInfo(pkgInfo2, tstPkgInfo2);
		SPDXPackageInfo tstPkgInfo3 = pkgInfoSheet.getPackageInfo(3);
		if (tstPkgInfo3 != null) {
			fail("should be null");
		}
	}

	private void comparePkgInfo(SPDXPackageInfo pkgInfo1,
			SPDXPackageInfo pkgInfo2) {
		assertEquals(pkgInfo1.getDeclaredCopyright(), pkgInfo2.getDeclaredCopyright());
		assertEquals(pkgInfo1.getVersionInfo(), pkgInfo2.getVersionInfo());
		assertEquals(pkgInfo1.getDeclaredLicenses(), pkgInfo2.getDeclaredLicenses());
		assertEquals(pkgInfo1.getConcludedLicense(), pkgInfo2.getConcludedLicense());
		assertEquals(pkgInfo1.getDeclaredName(), pkgInfo2.getDeclaredName());
		assertEquals(pkgInfo1.getDescription(), pkgInfo2.getDescription());
		assertEquals(pkgInfo1.getPackageVerification().getValue(), pkgInfo2.getPackageVerification().getValue());
		assertEquals(pkgInfo1.getFileName(), pkgInfo2.getFileName());
		assertEquals(pkgInfo1.getSha1(), pkgInfo2.getSha1());
		assertEquals(pkgInfo1.getShortDescription(), pkgInfo2.getShortDescription());
		assertEquals(pkgInfo1.getSourceInfo(), pkgInfo2.getSourceInfo());
		assertEquals(pkgInfo1.getUrl(), pkgInfo2.getUrl());
		if (!compareLicenses(pkgInfo1.getLicensesFromFiles(), pkgInfo2.getLicensesFromFiles())) {
			fail("license information in files not equal");
		}
		assertEquals(pkgInfo1.getPackageVerification().getValue(),
				pkgInfo2.getPackageVerification().getValue());
		assertEquals(pkgInfo1.getPackageVerification().getExcludedFileNames().length,
				pkgInfo2.getPackageVerification().getExcludedFileNames().length);
		for (int i = 0; i < pkgInfo1.getPackageVerification().getExcludedFileNames().length; i++) {
			assertEquals(pkgInfo1.getPackageVerification().getExcludedFileNames()[i], 
					pkgInfo2.getPackageVerification().getExcludedFileNames()[i]);
		}
		assertEquals(pkgInfo1.getSupplier(), pkgInfo2.getSupplier());
		assertEquals(pkgInfo1.getOriginator(), pkgInfo2.getOriginator());
		assertEquals(pkgInfo1.getHomePage(), pkgInfo2.getHomePage());		
	}

	/**
	 * @param license1
	 * @param licenses2
	 */
	private boolean compareLicenses(AnyLicenseInfo[] licenses1,
			AnyLicenseInfo[] licenses2) {
		if (licenses1.length != licenses2.length) {
			return false;
		}
		for (int i = 0; i < licenses1.length; i++) {
			boolean found = false;
			for (int j = 0; j < licenses2.length; j++) {
				if (licenses1[i].equals(licenses2[j])) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	static public void compareLicenseDeclarations(
			AnyLicenseInfo[] testLicenses,
			AnyLicenseInfo[] result) {
		assertEquals(testLicenses.length, result.length);
		for (int i = 0;i < testLicenses.length; i++) {
			boolean found = false;
			for (int j = 0; j < result.length; j++) {
				if (testLicenses[i].equals(result[j])) {
					found = true;
				}
			}				
			if (!found) {
				fail("License match "+testLicenses[i].toString()+ " was not found.");
			}
		}
	}
}
