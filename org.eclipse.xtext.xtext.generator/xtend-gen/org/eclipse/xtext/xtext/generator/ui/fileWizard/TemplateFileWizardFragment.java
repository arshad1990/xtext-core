/**
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator.ui.fileWizard;

import com.google.common.annotations.Beta;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtend2.lib.StringConcatenationClient;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xtext.generator.AbstractXtextGeneratorFragment;
import org.eclipse.xtext.xtext.generator.XtextGeneratorNaming;
import org.eclipse.xtext.xtext.generator.model.BinaryFileAccess;
import org.eclipse.xtext.xtext.generator.model.FileAccessFactory;
import org.eclipse.xtext.xtext.generator.model.ManifestAccess;
import org.eclipse.xtext.xtext.generator.model.PluginXmlAccess;
import org.eclipse.xtext.xtext.generator.model.TypeReference;
import org.eclipse.xtext.xtext.generator.model.XtendFileAccess;
import org.eclipse.xtext.xtext.generator.model.project.IBundleProjectConfig;

/**
 * Add a new file wizard with an (optional) template selection page.
 * 
 * Example usage:
 * <pre>
 * component = XtextGenerator {
 *     language = StandardLanguage {
 *         fileWizard = {
 *             generate = true
 *         }
 *     }
 * }
 * </pre>
 * 
 * @author Arne Deutsch - Initial contribution and API
 * @since 2.14
 */
@Beta
@SuppressWarnings("all")
public class TemplateFileWizardFragment extends AbstractXtextGeneratorFragment {
  @Inject
  @Extension
  private XtextGeneratorNaming _xtextGeneratorNaming;
  
  @Inject
  private FileAccessFactory fileAccessFactory;
  
  @Accessors
  private boolean generate = false;
  
  @Override
  public void generate() {
    if ((!this.generate)) {
      return;
    }
    IBundleProjectConfig _eclipsePlugin = this.getProjectConfig().getEclipsePlugin();
    ManifestAccess _manifest = null;
    if (_eclipsePlugin!=null) {
      _manifest=_eclipsePlugin.getManifest();
    }
    boolean _tripleNotEquals = (_manifest != null);
    if (_tripleNotEquals) {
      Set<String> _requiredBundles = this.getProjectConfig().getEclipsePlugin().getManifest().getRequiredBundles();
      Iterables.<String>addAll(_requiredBundles, Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList("org.eclipse.core.runtime", "org.eclipse.core.resources", "org.eclipse.ui", "org.eclipse.ui.ide", "org.eclipse.ui.forms")));
      Set<String> _exportedPackages = this.getProjectConfig().getEclipsePlugin().getManifest().getExportedPackages();
      String _eclipsePluginBasePackage = this._xtextGeneratorNaming.getEclipsePluginBasePackage(this.getGrammar());
      String _plus = (_eclipsePluginBasePackage + ".wizard");
      Iterables.<String>addAll(_exportedPackages, Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList(_plus)));
    }
    IBundleProjectConfig _eclipsePlugin_1 = this.getProjectConfig().getEclipsePlugin();
    PluginXmlAccess _pluginXml = null;
    if (_eclipsePlugin_1!=null) {
      _pluginXml=_eclipsePlugin_1.getPluginXml();
    }
    boolean _tripleNotEquals_1 = (_pluginXml != null);
    if (_tripleNotEquals_1) {
      List<CharSequence> _entries = this.getProjectConfig().getEclipsePlugin().getPluginXml().getEntries();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<extension");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("point=\"org.eclipse.ui.newWizards\">");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<category id=\"");
      String _eclipsePluginBasePackage_1 = this._xtextGeneratorNaming.getEclipsePluginBasePackage(this.getGrammar());
      _builder.append(_eclipsePluginBasePackage_1, "\t");
      _builder.append(".category\" name=\"");
      String _simpleName = GrammarUtil.getSimpleName(this.getGrammar());
      _builder.append(_simpleName, "\t");
      _builder.append("\">");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("</category>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<wizard");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("category=\"");
      String _eclipsePluginBasePackage_2 = this._xtextGeneratorNaming.getEclipsePluginBasePackage(this.getGrammar());
      _builder.append(_eclipsePluginBasePackage_2, "\t\t");
      _builder.append(".category\"");
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t");
      _builder.append("class=\"");
      TypeReference _eclipsePluginExecutableExtensionFactory = this._xtextGeneratorNaming.getEclipsePluginExecutableExtensionFactory(this.getGrammar());
      _builder.append(_eclipsePluginExecutableExtensionFactory, "\t\t");
      _builder.append(":org.eclipse.xtext.ui.wizard.template.TemplateNewFileWizard\"");
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t");
      _builder.append("id=\"");
      String _fileWizardClassName = this.getFileWizardClassName();
      _builder.append(_fileWizardClassName, "\t\t");
      _builder.append("\"");
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t");
      _builder.append("name=\"");
      String _simpleName_1 = GrammarUtil.getSimpleName(this.getGrammar());
      _builder.append(_simpleName_1, "\t\t");
      _builder.append(" File\"");
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t");
      _builder.append("icon=\"icons/new_");
      String _simpleName_2 = GrammarUtil.getSimpleName(this.getGrammar());
      _builder.append(_simpleName_2, "\t\t");
      _builder.append("_file.png\">");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("</wizard>");
      _builder.newLine();
      _builder.append("</extension>");
      _builder.newLine();
      _builder.append("<extension");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("point=\"org.eclipse.xtext.ui.fileTemplate\">");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("<fileTemplateProvider");
      _builder.newLine();
      _builder.append("         ");
      _builder.append("class=\"");
      String _fileTemplateProviderClassName = this.getFileTemplateProviderClassName();
      _builder.append(_fileTemplateProviderClassName, "         ");
      _builder.append("\"");
      _builder.newLineIfNotEmpty();
      _builder.append("         ");
      _builder.append("grammarName=\"");
      String _languageId = GrammarUtil.getLanguageId(this.getGrammar());
      _builder.append(_languageId, "         ");
      _builder.append("\">");
      _builder.newLineIfNotEmpty();
      _builder.append("   ");
      _builder.append("</fileTemplateProvider>");
      _builder.newLine();
      _builder.append("</extension>");
      _builder.newLine();
      _builder.append("<extension");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("point=\"org.eclipse.ui.perspectiveExtensions\">");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<perspectiveExtension targetID=\"org.eclipse.ui.resourcePerspective\">");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("<newWizardShortcut id=\"");
      String _fileWizardClassName_1 = this.getFileWizardClassName();
      _builder.append(_fileWizardClassName_1, "\t\t");
      _builder.append("\"/>");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("</perspectiveExtension>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<perspectiveExtension targetID=\"org.eclipse.jdt.ui.JavaPerspective\">");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("<newWizardShortcut id=\"");
      String _fileWizardClassName_2 = this.getFileWizardClassName();
      _builder.append(_fileWizardClassName_2, "\t\t");
      _builder.append("\"/>");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("</perspectiveExtension>");
      _builder.newLine();
      _builder.append("</extension>");
      _builder.newLine();
      _entries.add(_builder.toString());
    }
    this.generateProjectTemplateProvider();
    this.generateDefaultIcons();
  }
  
  public void generateProjectTemplateProvider() {
    final TypeReference initialContentsClass = TypeReference.typeRef(this.getFileTemplateProviderClassName());
    final String quotes = "\'\'\'";
    final String openVar = "�";
    final String closeVar = "�";
    final XtendFileAccess file = this.fileAccessFactory.createXtendFile(initialContentsClass);
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
        _builder.append("import org.eclipse.xtext.ui.wizard.template.FileTemplate");
        _builder.newLine();
        _builder.append("import org.eclipse.xtext.ui.wizard.template.IFileGenerator");
        _builder.newLine();
        _builder.append("import org.eclipse.xtext.ui.wizard.template.IFileTemplateProvider");
        _builder.newLine();
        _builder.newLine();
        _builder.append("/**");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("* Create a list with all file templates to be shown in the template new file wizard.");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("* ");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("* Each template is able to generate one or more files.");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("class ");
        String _simpleName = initialContentsClass.getSimpleName();
        _builder.append(_simpleName);
        _builder.append(" implements IFileTemplateProvider {");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("override getFileTemplates() {");
        _builder.newLine();
        _builder.append("\t\t");
        _builder.append("#[new HelloWorldFile]");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("}");
        _builder.newLine();
        _builder.append("}");
        _builder.newLine();
        _builder.newLine();
        _builder.append("@FileTemplate(label=\"Hello World\", icon=\"file_template.png\", description=\"Create a hello world for ");
        String _simpleName_1 = GrammarUtil.getSimpleName(TemplateFileWizardFragment.this.getGrammar());
        _builder.append(_simpleName_1);
        _builder.append(".\")");
        _builder.newLineIfNotEmpty();
        _builder.append("final class HelloWorldFile {");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("val helloName = combo(\"Hello Name:\", #[\"Xtext\", \"World\", \"Foo\", \"Bar\"], \"The name to say \'Hello\' to\")");
        _builder.newLine();
        _builder.newLine();
        _builder.append("\t");
        _builder.append("override generateFiles(IFileGenerator generator) {");
        _builder.newLine();
        _builder.append("\t\t");
        _builder.append("generator.generate(");
        _builder.append(quotes, "\t\t");
        _builder.append(openVar, "\t\t");
        _builder.append("folder");
        _builder.append(closeVar, "\t\t");
        _builder.append("/");
        _builder.append(openVar, "\t\t");
        _builder.append("name");
        _builder.append(closeVar, "\t\t");
        _builder.append(".");
        String _get = TemplateFileWizardFragment.this.getLanguage().getFileExtensions().get(0);
        _builder.append(_get, "\t\t");
        _builder.append(quotes, "\t\t");
        _builder.append(", ");
        _builder.append(quotes, "\t\t");
        _builder.newLineIfNotEmpty();
        _builder.append("\t\t\t");
        _builder.append("/*");
        _builder.newLine();
        _builder.append("\t\t\t ");
        _builder.append("* This is an example model");
        _builder.newLine();
        _builder.append("\t\t\t ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("\t\t\t");
        _builder.append("Hello ");
        _builder.append(openVar, "\t\t\t");
        _builder.append("helloName");
        _builder.append(closeVar, "\t\t\t");
        _builder.append("!");
        _builder.newLineIfNotEmpty();
        _builder.append("\t\t");
        _builder.append(quotes, "\t\t");
        _builder.append(")");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("}");
        _builder.newLine();
        _builder.append("}");
        _builder.newLine();
      }
    };
    file.setContent(_client);
    file.writeTo(this.getProjectConfig().getEclipsePlugin().getSrc());
  }
  
  public void generateDefaultIcons() {
    final BinaryFileAccess projectTemplate = this.fileAccessFactory.createBinaryFile("file_template.png");
    projectTemplate.setContent(this.readBinaryFileFromPackage("file_template.png"));
    projectTemplate.writeTo(this.getProjectConfig().getEclipsePlugin().getIcons());
    String _simpleName = GrammarUtil.getSimpleName(this.getGrammar());
    String _plus = ("new_" + _simpleName);
    String _plus_1 = (_plus + "_file.png");
    final BinaryFileAccess newProject = this.fileAccessFactory.createBinaryFile(_plus_1);
    newProject.setContent(this.readBinaryFileFromPackage("new_xfile.png"));
    newProject.writeTo(this.getProjectConfig().getEclipsePlugin().getIcons());
  }
  
  private byte[] readBinaryFileFromPackage(final String fileName) {
    try {
      final InputStream stream = TemplateFileWizardFragment.class.getResourceAsStream(fileName);
      try {
        return ByteStreams.toByteArray(stream);
      } finally {
        stream.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected String getFileTemplateProviderClassName() {
    String _fileWizardPackage = this.getFileWizardPackage();
    String _simpleName = GrammarUtil.getSimpleName(this.getGrammar());
    String _plus = (_fileWizardPackage + _simpleName);
    return (_plus + "FileTemplateProvider");
  }
  
  protected String getFileWizardClassName() {
    String _fileWizardPackage = this.getFileWizardPackage();
    String _simpleName = GrammarUtil.getSimpleName(this.getGrammar());
    String _plus = (_fileWizardPackage + _simpleName);
    return (_plus + "NewFileWizard");
  }
  
  protected String getFileWizardPackage() {
    String _eclipsePluginBasePackage = this._xtextGeneratorNaming.getEclipsePluginBasePackage(this.getGrammar());
    return (_eclipsePluginBasePackage + ".wizard.");
  }
  
  /**
   * Generate the wizard. Set to 'false' by default. Change to 'true' to generate the wizard.
   */
  public boolean setGenerate(final boolean value) {
    return this.generate = value;
  }
  
  @Pure
  public boolean isGenerate() {
    return this.generate;
  }
}
