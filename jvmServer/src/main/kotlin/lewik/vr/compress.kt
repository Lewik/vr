package lewik.vr

import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream


fun ByteArray.compress(): ByteArray {
    val out = ByteArrayOutputStream()
    val defl = DeflaterOutputStream(out)
    defl.write(this)
    defl.flush()
    defl.close()
    return out.toByteArray()
}

fun ByteArray.decompress(): ByteArray {
    val out = ByteArrayOutputStream()
    val inflater = InflaterOutputStream(out)
    inflater.write(this)
    inflater.flush()
    inflater.close()
    return out.toByteArray()
}