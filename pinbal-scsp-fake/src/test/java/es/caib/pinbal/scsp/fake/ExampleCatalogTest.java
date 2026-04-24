package es.caib.pinbal.scsp.fake;

import es.caib.pinbal.scsp.fake.ExampleCatalog.ServiceExamples;
import org.junit.Assert;
import org.junit.Test;

public class ExampleCatalogTest {

	@Test
	public void shouldLoadServicesFromExamplesDocument() throws Exception {
		ExampleCatalog catalog = ExampleCatalog.loadDefault();

		ServiceExamples tgss = catalog.getService("Q2827003ATGSS001");
		Assert.assertNotNull(tgss);
		Assert.assertNotNull(tgss.getSyncResponse());
		Assert.assertNotNull(tgss.getAsyncConfirmation());
		Assert.assertNotNull(tgss.getAsyncResponse());

		ServiceExamples padro = catalog.getService("SCDCPAJU");
		Assert.assertNotNull(padro);
		Assert.assertNotNull(padro.getSyncResponse());

		ServiceExamples policia = catalog.getService("SVDDGPCIWS02");
		Assert.assertNotNull(policia);
		Assert.assertNotNull(policia.getSyncResponse());
	}
}
