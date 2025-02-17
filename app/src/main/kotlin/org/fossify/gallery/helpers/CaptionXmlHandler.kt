package org.fossify.gallery.helpers

import android.util.Xml
import org.fossify.gallery.models.Caption
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.io.OutputStream


// 工具单例类
object CaptionXmlHandler {
    fun export(captions: List<Caption>, outputStream: OutputStream) {
        val serializer = Xml.newSerializer()
        serializer.setOutput(outputStream, StandardCharsets.UTF_8.name())
        serializer.startDocument(StandardCharsets.UTF_8.name(), true)
        serializer.startTag(null, "captions")

        for (caption in captions) {
            serializer.startTag(null, "caption")

            serializer.startTag(null, "filename")
            serializer.text(caption.name)
            serializer.endTag(null, "filename")

            serializer.startTag(null, "full_path")
            serializer.text(caption.path)
            serializer.endTag(null, "full_path")

            serializer.startTag(null, "type")
            serializer.text(caption.type)
            serializer.endTag(null, "type")

            serializer.startTag(null, "content")
            serializer.text(caption.content)
            serializer.endTag(null, "content")

            serializer.endTag(null, "caption")
        }

        serializer.endTag(null, "captions")
        serializer.endDocument()
    }


    fun import(inputStream: InputStream): List<Caption> {
        val captions = mutableListOf<Caption>()
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var eventType = parser.eventType
        var currentCaption: Caption? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "caption" -> currentCaption = Caption()
                        "filename" -> currentCaption?.name = parser.nextText()
                        "full_path" -> currentCaption?.path = parser.nextText()
                        "type" -> currentCaption?.type = parser.nextText()
                        "content" -> currentCaption?.content = parser.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "caption" && currentCaption != null) {
                        captions.add(currentCaption)
                        currentCaption = null
                    }
                }
            }
            eventType = parser.next()
        }

        return captions
    }
}
