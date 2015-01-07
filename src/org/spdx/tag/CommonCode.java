/** * Copyright (c) 2010 Source Auditor Inc. * *   Licensed under the Apache License, Version 2.0 (the "License"); *   you may not use this file except in compliance with the License. *   You may obtain a copy of the License at * *       http://www.apache.org/licenses/LICENSE-2.0 * *   Unless required by applicable law or agreed to in writing, software *   distributed under the License is distributed on an "AS IS" BASIS, *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *   See the License for the specific language governing permissions and *   limitations under the License. * */package org.spdx.tag;import java.io.IOException;import java.io.InputStream;import java.io.PrintWriter;import java.util.ArrayList;import java.util.Arrays;import java.util.Collections;import java.util.List;import java.util.Properties;import org.spdx.rdfparser.DOAPProject;import org.spdx.rdfparser.InvalidSPDXAnalysisException;import org.spdx.rdfparser.SPDXDocument;import org.spdx.rdfparser.SPDXDocument.SPDXPackage;import org.spdx.rdfparser.license.AnyLicenseInfo;import org.spdx.rdfparser.license.ExtractedLicenseInfo;import org.spdx.rdfparser.license.SpdxListedLicense;import org.spdx.rdfparser.SPDXFile;import org.spdx.rdfparser.SPDXReview;import org.spdx.tools.RdfToTag;/** * Define Common methods used by Tag-Value and SPDXViewer to print the SPDX * document. *  * @author Rana Rahal, Protecode Inc. */public class CommonCode {		/**	 * @param doc	 * @param out	 * @param constants	 * @throws InvalidSPDXAnalysisException	 */	public static void printDoc(SPDXDocument doc, PrintWriter out,			Properties constants) throws InvalidSPDXAnalysisException {		if (doc == null) {			println(out, "Warning: No document to print");			return;		}		String spdxVersion = "";		if (doc.getSpdxVersion() != null				&& doc.getCreatorInfo().getCreated() != null) {			spdxVersion = doc.getSpdxVersion();			println(out, constants.getProperty("PROP_SPDX_VERSION") + spdxVersion);		}		if (!spdxVersion.equals(SPDXDocument.POINT_EIGHT_SPDX_VERSION)				&& !spdxVersion.equals(SPDXDocument.POINT_NINE_SPDX_VERSION)) {			SpdxListedLicense dataLicense = doc.getDataLicense();			if (dataLicense != null) {				println(out, constants.getProperty("PROP_SPDX_DATA_LICENSE")						+ dataLicense.getName());			}		}		if (doc.getDocumentComment() != null && !doc.getDocumentComment().isEmpty()) {			println(out, constants.getProperty("PROP_SPDX_COMMENT")					+ constants.getProperty("PROP_BEGIN_TEXT")					+ doc.getDocumentComment()					+ constants.getProperty("PROP_END_TEXT"));		}		println(out, "");		if (doc.getCreatorInfo().getCreators() != null				&& doc.getCreatorInfo().getCreators().length > 0) {			println(out, constants.getProperty("CREATION_INFO_HEADER"));			String[] creators = doc.getCreatorInfo().getCreators();			for (int i = 0; i < creators.length; i++) {				println(out, constants.getProperty("PROP_CREATION_CREATOR")						+ creators[i]);			}		}		if (doc.getCreatorInfo().getCreated() != null				&& !doc.getCreatorInfo().getCreated().isEmpty()) {			println(out, constants.getProperty("PROP_CREATION_CREATED")					+ doc.getCreatorInfo().getCreated());		}		if (doc.getCreatorInfo().getLicenseListVersion() != null &&				!doc.getCreatorInfo().getLicenseListVersion().isEmpty()) {			println(out, constants.getProperty("PROP_LICENSE_LIST_VERSION") + 					doc.getCreatorInfo().getLicenseListVersion());		}		if (doc.getCreatorInfo().getComment() != null				&& !doc.getCreatorInfo().getComment().isEmpty()) {			println(out, constants.getProperty("PROP_CREATION_COMMENT")					+ constants.getProperty("PROP_BEGIN_TEXT") 					+ doc.getCreatorInfo().getComment() 					+ constants.getProperty("PROP_END_TEXT"));		}		println(out, "");		if (doc.getReviewers() != null && doc.getReviewers().length > 0) {			println(out, constants.getProperty("REVIEW_INFO_HEADER"));			SPDXReview[] reviewedBy = doc.getReviewers();			for (int i = 0; i < reviewedBy.length; i++) {				println(out, constants.getProperty("PROP_REVIEW_REVIEWER")						+ reviewedBy[i].getReviewer());				println(out, constants.getProperty("PROP_REVIEW_DATE")						+ reviewedBy[i].getReviewDate());				if (reviewedBy[i].getComment() != null						&& !reviewedBy[i].getComment().isEmpty()) {					println(out, constants.getProperty("PROP_REVIEW_COMMENT")							+ constants.getProperty("PROP_BEGIN_TEXT") 							+ reviewedBy[i].getComment() 							+ constants.getProperty("PROP_END_TEXT"));				}				println(out, "");			}		}		printPackage(doc.getSpdxPackage(), out, constants);		println(out, "");		if (doc.getExtractedLicenseInfos() != null				&& doc.getExtractedLicenseInfos().length > 0) {			ExtractedLicenseInfo[] nonStandardLic = doc					.getExtractedLicenseInfos();			println(out, constants.getProperty("LICENSE_INFO_HEADER"));			for (int i = 0; i < nonStandardLic.length; i++) {				printLicense(nonStandardLic[i], out, constants);			}		}	}	/**	 * @param license	 */	private static void printLicense(ExtractedLicenseInfo license,			PrintWriter out, Properties constants) {		// id		if (license.getLicenseId() != null && !license.getLicenseId().isEmpty()) {			println(out,					constants.getProperty("PROP_LICENSE_ID") + license.getLicenseId());		}		if (license.getExtractedText() != null && !license.getExtractedText().isEmpty()) {			println(out, constants.getProperty("PROP_EXTRACTED_TEXT") 					+ constants.getProperty("PROP_BEGIN_TEXT")					+ license.getExtractedText() + constants.getProperty("PROP_END_TEXT"));		}		if (license.getName() != null && !license.getName().isEmpty()) {			println(out, constants.getProperty("PROP_LICENSE_NAME")+license.getName());		}		if (license.getSeeAlso() != null && license.getSeeAlso().length > 0) {			StringBuilder sb = new StringBuilder();			sb.append(license.getSeeAlso()[0]);			for (int i = 1; i < license.getSeeAlso().length; i++) {				sb.append(", ");				sb.append(license.getSeeAlso()[i]);			}			println(out, constants.getProperty("PROP_SOURCE_URLS")+sb.toString());		}		if (license.getSeeAlso() != null)		if (license.getComment() != null && !license.getComment().isEmpty()) {			println(out, constants.getProperty("PROP_LICENSE_COMMENT")					+ constants.getProperty("PROP_BEGIN_TEXT")					+ license.getComment()					+ constants.getProperty("PROP_END_TEXT"));		}		println(out, "");	}	/**	 * @param spdxPackage	 * @throws InvalidSPDXAnalysisException	 */	private static void printPackage(SPDXPackage pkg, PrintWriter out,			Properties constants) throws InvalidSPDXAnalysisException {		println(out, constants.getProperty("PACKAGE_INFO_HEADER"));		// Declared name		if (pkg.getDeclaredName() != null && !pkg.getDeclaredName().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_DECLARED_NAME")					+ pkg.getDeclaredName());		}		// Version		if (pkg.getVersionInfo() != null && !pkg.getVersionInfo().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_VERSION_INFO")							+ pkg.getVersionInfo());		}		// Home page		if (pkg.getHomePage() != null && !pkg.getHomePage().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_HOMEPAGE_URL") + 					pkg.getHomePage());		}		// Download location		if (pkg.getDownloadUrl() != null && !pkg.getDownloadUrl().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_DOWNLOAD_URL")							+ pkg.getDownloadUrl());		}		// Short description		if (pkg.getShortDescription() != null				&& !pkg.getShortDescription().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_SHORT_DESC")					+ constants.getProperty("PROP_BEGIN_TEXT") 					+ pkg.getShortDescription() + constants.getProperty("PROP_END_TEXT"));		}		// Source info		if (pkg.getSourceInfo() != null && !pkg.getSourceInfo().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_SOURCE_INFO")							+ pkg.getSourceInfo());		}		// File name		if (pkg.getFileName() != null && !pkg.getFileName().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_FILE_NAME")							+ pkg.getFileName());		}		// Supplier		if (pkg.getSupplier() != null && !pkg.getSupplier().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_SUPPLIER")							+ pkg.getSupplier());		}		// Originator		if (pkg.getOriginator() != null && !pkg.getOriginator().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_ORIGINATOR")							+ pkg.getOriginator());		}		// sha1		if (pkg.getSha1() != null && !pkg.getSha1().isEmpty()) {			println(out,					constants.getProperty("PROP_PACKAGE_CHECKSUM")							+ pkg.getSha1());		}		// package verification code        if (pkg.getVerificationCode() != null                && pkg.getVerificationCode().getValue() != null                && !pkg.getVerificationCode().getValue().isEmpty()) {          String code = constants.getProperty("PROP_PACKAGE_VERIFICATION_CODE") + pkg.getVerificationCode().getValue();          String excludedFilesString = "";          String[] excludedFiles = pkg.getVerificationCode().getExcludedFileNames();          if (excludedFiles.length != 0) {                excludedFilesString = " (" + excludedFiles[0];                for (int i=1; i<excludedFiles.length; i++ ) {                       excludedFilesString += ", " + excludedFiles[i];                }                excludedFilesString += ")";                code += excludedFilesString;          }                              println(out, code);        }		// Description		if (pkg.getDescription() != null && !pkg.getDescription().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_DESCRIPTION")					+ constants.getProperty("PROP_BEGIN_TEXT") 					+ pkg.getDescription() + constants.getProperty("PROP_END_TEXT"));		}		println(out, "");		// Declared copyright		if (pkg.getDeclaredCopyright() != null				&& !pkg.getDeclaredCopyright().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_DECLARED_COPYRIGHT")					+ constants.getProperty("PROP_BEGIN_TEXT") 					+ pkg.getDeclaredCopyright() + constants.getProperty("PROP_END_TEXT"));		}		println(out, "");		// Declared licenses		if (pkg.getDeclaredLicense() != null) {			println(out, constants.getProperty("PROP_PACKAGE_DECLARED_LICENSE")					+ pkg.getDeclaredLicense());		}		// concluded license		if (pkg.getConcludedLicenses() != null) {			println(out, constants.getProperty("PROP_PACKAGE_CONCLUDED_LICENSE")					+ pkg.getConcludedLicenses());		}		// file licenses		if (pkg.getLicenseInfoFromFiles() != null				&& pkg.getLicenseInfoFromFiles().length > 0) {			AnyLicenseInfo[] licenses = pkg.getLicenseInfoFromFiles();			println(out, constants.getProperty("LICENSE_FROM_FILES_INFO_HEADER"));			for (int i = 0; i < licenses.length; i++) {				println(out,						constants								.getProperty("PROP_PACKAGE_LICENSE_INFO_FROM_FILES")								+ licenses[i].toString());			}		}		if (pkg.getLicenseComment() != null				&& !pkg.getLicenseComment().isEmpty()) {			println(out, constants.getProperty("PROP_PACKAGE_LICENSE_COMMENT")					+ constants.getProperty("PROP_BEGIN_TEXT") 					+ pkg.getLicenseComment() + 					constants.getProperty("PROP_END_TEXT"));		}		// Files		if (pkg.getFiles() != null && pkg.getFiles().length > 0) {                    /* Add files to a List */                    List<SPDXFile> sortedFileList = new ArrayList<SPDXFile>();                    /* Sort the SPDX files before printout */                    sortedFileList = Arrays.asList(pkg.getFiles());                    Collections.sort(sortedFileList);                                        println(out, "");			println(out, constants.getProperty("FILE_INFO_HEADER"));                        /* Print out sorted files */			for (SPDXFile file : sortedFileList) {				printFile(file, out, constants);				println(out, "");			}		}	}	/**	 * @param file	 */	private static void printFile(SPDXFile file, PrintWriter out,			Properties constants) {		// name		if (file.getName() != null && !file.getName().isEmpty()) {			println(out, constants.getProperty("PROP_FILE_NAME") + file.getName());		}		// type		if (file.getType() != null && !file.getType().isEmpty()) {			println(out, constants.getProperty("PROP_FILE_TYPE") + file.getType());		}		// sha1		if (file.getSha1() != null && !file.getSha1().isEmpty()) {			println(out,					constants.getProperty("PROP_FILE_CHECKSUM")							+ file.getSha1());		}		// concluded license		if (file.getConcludedLicenses() != null) {			println(out, constants.getProperty("PROP_FILE_LICENSE")					+ file.getConcludedLicenses().toString());		}		// License info in file		if (file.getSeenLicenses() != null && file.getSeenLicenses().length > 0) {			// print(out, "\tLicense information from file: ");			// print(out, file.getSeenLicenses()[0].toString());			for (int i = 0; i < file.getSeenLicenses().length; i++) {				println(out, constants.getProperty("PROP_FILE_SEEN_LICENSE")						+ file.getSeenLicenses()[i].toString());			}			// print(out, );		}		// license comments		if (file.getLicenseComments() != null				&& !file.getLicenseComments().isEmpty()) {			println(out,					constants.getProperty("PROP_FILE_LIC_COMMENTS")							+ file.getLicenseComments());		}		// file copyright		if (file.getCopyright() != null && !file.getCopyright().isEmpty()) {			println(out, constants.getProperty("PROP_FILE_COPYRIGHT") 					+ constants.getProperty("PROP_BEGIN_TEXT")					+ file.getCopyright() + constants.getProperty("PROP_END_TEXT"));		}		// artifact of		if (file.getArtifactOf() != null && file.getArtifactOf().length > 0) {			for (int i = 0; i < file.getArtifactOf().length; i++) {				printProject(file.getArtifactOf()[i], out, constants);			}		}		// file dependencies		if (file.getFileDependencies() != null && file.getFileDependencies().length > 0) {			for (int i = 0; i < file.getFileDependencies().length; i++) {				println(out, constants.getProperty("PROP_FILE_DEPENDENCY") + 						file.getFileDependencies()[i].getName());			}		}		// file contributors		if (file.getContributors() != null && file.getContributors().length > 0) {			for (int i = 0; i < file.getContributors().length; i++) {				println(out, constants.getProperty("PROP_FILE_CONTRIBUTOR")+						file.getContributors()[i]);			}		}		// File notice		if (file.getNoticeText() != null && !file.getNoticeText().isEmpty()) {			println(out, constants.getProperty("PROP_FILE_NOTICE_TEXT") + 					constants.getProperty("PROP_BEGIN_TEXT") +					file.getNoticeText() + 					constants.getProperty("PROP_END_TEXT"));		}		// file comment		if (file.getComment() != null && !file.getComment().isEmpty()) {			println(out, constants.getProperty("PROP_FILE_COMMENT") 					+ constants.getProperty("PROP_BEGIN_TEXT")					+ file.getComment() + constants.getProperty("PROP_END_TEXT"));		}	}	/**	 * @param doapProject	 */	private static void printProject(DOAPProject doapProject, PrintWriter out,			Properties constants) {		// project name		if (doapProject.getName() != null && !doapProject.getName().isEmpty()) {			println(out,					constants.getProperty("PROP_PROJECT_NAME")							+ doapProject.getName());		}		// project homepage		if (doapProject.getHomePage() != null				&& !doapProject.getHomePage().isEmpty()) {			println(out, constants.getProperty("PROP_PROJECT_HOMEPAGE")					+ doapProject.getHomePage());		}		// DOAP file url		if (doapProject.getProjectUri() != null				&& !doapProject.getProjectUri().isEmpty()				&& !doapProject.getProjectUri().equals(DOAPProject.UNKNOWN_URI)) {			println(out,					constants.getProperty("PROP_PROJECT_URI")							+ doapProject.getProjectUri());		}	}	private static void println(PrintWriter out, String output) {		if (out != null) {			out.println(output);		} else {			System.out.println(output);		}	}	public static Properties getTextFromProperties(final String path)			throws IOException {		InputStream is = null;		Properties prop = new Properties();		try {			is = RdfToTag.class.getClassLoader().getResourceAsStream(path);			prop.load(is);		} finally {			try {				if (is != null) {					is.close();				}			} catch (Throwable e) {//				logger.warn("Unable to close properties file.");			}		}		return prop;	}}