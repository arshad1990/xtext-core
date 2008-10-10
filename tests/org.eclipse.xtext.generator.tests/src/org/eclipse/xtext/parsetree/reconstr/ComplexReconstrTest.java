/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.parsetree.reconstr;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.tests.AbstractGeneratorTest;
import org.eclipse.xtext.tests.EcoreModelComparator;
import org.eclipse.xtext.util.EmfFormater;

public class ComplexReconstrTest extends AbstractGeneratorTest {

	private static final Logger logger = Logger.getLogger(ComplexReconstrTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		with(ComplexReconstrTestStandaloneSetup.class);
	}

	public void testPrintGrammar() {
		ResourceSet rs = new XtextResourceSet();
		URI u = URI
				.createURI("classpath:/org/eclipse/xtext/parsetree/reconstr/ComplexReconstrTest.xmi");
		EObject o = rs.getResource(u, true).getContents().get(0);
		for (Object x : o.eContents())
			if (x instanceof ParserRule) {
				ParserRule pr = (ParserRule) x;
				if (pr.getName().toLowerCase().contains("tricky")) {
					logger.debug(EmfFormater.objToStr(pr, ""));
				}
			}
	}

	public void testSimple() throws Exception {
		String model = "( a + b - c ) !";
		assertEquals(model, parseAndSerialize(model));
	}

	public void testComplex() throws Exception {
		String model = "( ( a + b ) ! - c + d + e + f - ( ^x + s ) - ( ( a + b ) ! - c ) ! ) !";
		assertEquals(model, parseAndSerialize(model));
	}

	private String parseAndSerialize(String model) throws Exception {
		EObject result = (EObject) getModel(model);
		logger.debug(EmfFormater.objToStr(result, ""));
		IParseTreeConstructor con = getParseTreeConstructor();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		con.serialize(out, result, Collections.emptyMap());
		return out.toString();
	}

	public void testNormalizableCompositeNodesIncluded() throws Exception {
		reconstructAndCompare("a");
		reconstructAndCompare("a + b");
	}

	private void reconstructAndCompare(String mymodel) throws Exception,
			InterruptedException {
		EObject model = getModel(mymodel);
		EObject model2 = getModel(parseAndSerialize(mymodel));
		EcoreModelComparator ecoreModelComparator = new EcoreModelComparator();
		assertFalse(ecoreModelComparator.modelsDiffer(model, model2));
	}

}
