package com.project.taskmanagercivil.platform

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement

/**
 * Helper para download de documentos no WASM
 *
 * Utiliza a API do browser para criar downloads de arquivos binários
 * a partir de ByteArray recebido do backend
 *
 * Uso:
 * ```kotlin
 * val bytes = repository.downloadDocument(documentId)
 * DocumentDownloadHelper.download(bytes, "documento.pdf", "application/pdf")
 * ```
 */
object DocumentDownloadHelper {

    /**
     * Baixa arquivo no browser
     *
     * Cria um data URL com base64 e usa um link temporário
     * para disparar o download no browser
     *
     * @param bytes Conteúdo do arquivo
     * @param filename Nome do arquivo (ex: "documento.pdf")
     * @param mimeType Tipo MIME do arquivo (padrão: "application/octet-stream")
     */
    fun download(
        bytes: ByteArray,
        filename: String,
        mimeType: String = "application/octet-stream"
    ) {
        try {
            // Converte bytes para base64
            val base64 = bytes.toBase64()
            
            // Cria data URL
            val dataUrl = "data:$mimeType;base64,$base64"

            // Cria elemento <a> temporário para disparar download
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = dataUrl
            anchor.download = filename
            anchor.style.display = "none"

            // Adiciona ao DOM, clica e remove
            document.body?.appendChild(anchor)
            anchor.click()
            document.body?.removeChild(anchor)

            println("✅ Download iniciado: $filename")

        } catch (e: Exception) {
            println("❌ Erro ao baixar arquivo: ${e.message}")
            throw Exception("Erro ao baixar arquivo: ${e.message}")
        }
    }

    /**
     * Converte ByteArray para Base64
     */
    private fun ByteArray.toBase64(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        var result = ""
        var i = 0
        
        while (i < this.size) {
            val b1 = this[i++].toInt() and 0xFF
            val b2 = if (i < this.size) this[i++].toInt() and 0xFF else 0
            val b3 = if (i < this.size) this[i++].toInt() and 0xFF else 0
            
            val enc1 = b1 shr 2
            val enc2 = ((b1 and 0x03) shl 4) or (b2 shr 4)
            val enc3 = ((b2 and 0x0F) shl 2) or (b3 shr 6)
            val enc4 = b3 and 0x3F
            
            result += chars[enc1]
            result += chars[enc2]
            result += if (i - 1 < this.size) chars[enc3] else '='
            result += if (i < this.size) chars[enc4] else '='
        }
        
        return result
    }

    /**
     * Retorna o MIME type baseado na extensão do arquivo
     *
     * @param filename Nome do arquivo com extensão
     * @return MIME type correspondente
     */
    fun getMimeType(filename: String): String {
        val extension = filename.substringAfterLast(".", "").lowercase()

        return when (extension) {
            "pdf" -> "application/pdf"
            "dwg" -> "application/acad"
            "dxf" -> "application/dxf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            else -> "application/octet-stream"
        }
    }

    /**
     * Baixa documento com detecção automática de MIME type
     *
     * @param bytes Conteúdo do arquivo
     * @param filename Nome do arquivo com extensão
     */
    fun downloadWithAutoMimeType(bytes: ByteArray, filename: String) {
        val mimeType = getMimeType(filename)
        download(bytes, filename, mimeType)
    }
}
