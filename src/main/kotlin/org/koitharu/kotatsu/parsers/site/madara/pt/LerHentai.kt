package org.koitharu.kotatsu.parsers.site.madara.pt

import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaSourceParser
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.site.madara.MadaraParser

@MangaSourceParser("LERHENTAI", "LerHentai", "pt", ContentType.HENTAI)
internal class LerHentai(context: MangaLoaderContext) :
	MadaraParser(context, MangaParserSource.LERHENTAI, "lerhentai.com", 20) {
	override val datePattern: String = "dd 'de' MMMMM 'de' yyyy"
}