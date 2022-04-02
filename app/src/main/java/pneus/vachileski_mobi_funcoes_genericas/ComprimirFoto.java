package pneus.vachileski_mobi_funcoes_genericas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings({"SpellCheckingInspection","UnusedReturnValue"})
public class ComprimirFoto {
    public boolean foto(String nomeFoto, String caminhoFoto, String tipoFoto) {
        File fotoCapturada = new File(GettersSetters.getFotoCapturada().getPath());
        boolean resultNovaFotoComprimida = false;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] photoByte;

        /* Diminui tamanho da imagem */
        String novoNomeFoto = "Compress-" + nomeFoto;
        Bitmap fotoBitmap = BitmapFactory.decodeFile(fotoCapturada.getAbsolutePath());

        Bitmap bitmapNovaEscala = Bitmap.createScaledBitmap(fotoBitmap, 768, 1024, false);

        /* Comprime para PNG qualidade 100 */
        bitmapNovaEscala.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        photoByte = outputStream.toByteArray();
        while (photoByte.length > 500000) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            photoByte = stream.toByteArray();

            if (photoByte.length < 500000) {
                break;
            }
        }

        final File novaFotoComprimida = new File(caminhoFoto + novoNomeFoto);
        try {
            /* Cria nova imagem comprimida **/
            resultNovaFotoComprimida = novaFotoComprimida.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.toString(), "Erro ao criar nova foto");
        } finally {
            try {
                FileOutputStream saidaFotoComprimida = new FileOutputStream(novaFotoComprimida);
                saidaFotoComprimida.write(photoByte);
                saidaFotoComprimida.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(e.toString(), "Erro ao FileOutputStream nova foto");
            }

            if (tipoFoto.equals("PermCli")) {
                GettersSetters.setPathAssinCli(caminhoFoto);
                GettersSetters.setPicNameAssinCli(novoNomeFoto);
            } else {
                GettersSetters.setPathDocsNovoCli(caminhoFoto);
                if (GettersSetters.getTipoDoc().equals("RG")) {
                    GettersSetters.setPicNameRGCli(novoNomeFoto);
                } else if (GettersSetters.getTipoDoc().equals("CPF/CNPJ")) {
                    GettersSetters.setPicNameDocCli(novoNomeFoto);
                } else {
                    GettersSetters.setPicNameComprResidCli(novoNomeFoto);
                }
            }

            @SuppressWarnings("unused")
            boolean delFotoCameraCapt = GettersSetters.getFotoCapturada().delete();
        }
        return resultNovaFotoComprimida;
    }
}
