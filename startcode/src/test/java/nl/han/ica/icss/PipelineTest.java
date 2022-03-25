package nl.han.ica.icss;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import nl.han.ica.icss.gui.MainGui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PipelineTest {

	private Pipeline sut;

	@BeforeEach
	void setUp() {
		sut = new Pipeline();
	}

	@Test
	void level0() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		               }
		               """;
		var actual = compile("level0.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level1() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		               }
		               """;
		var actual = compile("level1.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level2() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		               }
		               """;
		var actual = compile("level2.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level3() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		                 color: #124532;
		                 background-color: #000000;
		                 height: 20px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		                 background-color: #ff0000;
		               }
		               """;
		var actual = compile("level3.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level4() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		                 color: #124532;
		                 background-color: #000000;
		                 height: 20px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		                 background-color: #ff0000;
		               }
		               div, section {
		                 width: 500px;
		                 height: 500px;
		               }
		               """;
		var actual = compile("level4.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level5() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		                 color: #124532;
		                 background-color: #000000;
		                 height: 20px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		                 background-color: #ff0000;
		               }
		               div, section {
		                 width: 500px;
		                 height: 500px;
		               }
		               div p, section p {
		                 color: #ff0000;
		               }
		               div p a, section p a, div p span, section p span {
		                 color: #00ff00;
		               }
		               div article, section article {
		                 background-color: #000000;
		               }
		               div article p, section article p {
		                 color: #ffffff;
		               }
		               """;
		var actual = compile("level5.icss");
		assertEquals(expected, actual);
	}

	@Test
	void level6() throws IOException, URISyntaxException {
		var expected = """
		               p {
		                 background-color: #ffffff;
		                 width: 500px;
		                 color: #124532;
		                 background-color: #000000;
		                 height: 20px;
		               }
		               a {
		                 color: #ff0000;
		               }
		               #menu {
		                 width: 520px;
		               }
		               .menu {
		                 color: #000000;
		                 background-color: #ff0000;
		               }
		               div, section {
		                 width: 500px;
		                 height: 500px;
		               }
		               div p, section p {
		                 color: #ff0000;
		               }
		               div p a, section p a, div p span, section p span {
		                 color: #00ff00;
		               }
		               div article, section article {
		                 background-color: #000000;
		               }
		               div article p, section article p {
		                 color: #ffffff;
		               }
		               #line {
		                 width: 100%;
		                 height: 1px;
		                 background-color: #000000;
		               }
		               #button-a, #button-b {
		                 width: 80px;
		                 height: 40px;
		                 color: #00ff00;
		               }
		               #filled-button {
		                 width: 100px;
		                 height: 50px;
		                 color: #444444;
		                 background-color: #444444;
		               }
		               #highlighted-section {
		                 background-color: #000000;
		               }
		               #highlighted-section p {
		                 width: 500px;
		               }
		               #highlighted-section p a {
		                 color: #123546;
		               }
		               """;
		var actual = compile("level6.icss");
		assertEquals(expected, actual);
	}

	private String compile(String filename) throws IOException, URISyntaxException {
		var url = MainGui.class.getClassLoader().getResource(filename);
		if (url == null) {
			throw new IOException("Resource [" + filename + "] could not be found");
		}

		var input = Files.readString(Path.of(url.toURI()), Charset.defaultCharset());

		sut.parseString(input);
		sut.check();
		sut.transform();

		return sut.generate();
	}

}
