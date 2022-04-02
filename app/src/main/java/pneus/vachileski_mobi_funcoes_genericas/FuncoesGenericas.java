package pneus.vachileski_mobi_funcoes_genericas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pneus.vachileski_mobi_apanhe_pneus.BuildConfig;
import pneus.vachileski_mobi_apanhe_pneus.R;

public class FuncoesGenericas {
    String codigoVendedor = "";
    String seqIdentifColeta = "";
    String numeroColeta = "";
    String stringFormat = "";
    int maxColLocal = 0;

    @SuppressLint("DefaultLocale")
    public String geraNumeroColeta(Context context, boolean reenvio, ConexaoBDExt conecta, ConexaoBDInt db) {
        try {
            if (!GettersSetters.getIdUsuarioLogado().equals(GettersSetters.getCodigoVendedor())) {
                codigoVendedor = GettersSetters.getIdUsuarioLogado();
            } else {
                codigoVendedor = GettersSetters.getCodigoVendedor();
            }

            stringFormat = String.format("%06d", Integer.parseInt(codigoVendedor)).substring(2, 6);

            if (CheckConnection.isConnected(context)) {
                maxColLocal = db.selectMaxNumIdentificadorColeta(codigoVendedor);
                seqIdentifColeta = conecta.selecionaMaxNumColeta(codigoVendedor);

                if (maxColLocal > Integer.parseInt(seqIdentifColeta)) {
                    seqIdentifColeta = String.valueOf(maxColLocal);
                }

                if (reenvio) {
                    seqIdentifColeta = String.valueOf(Integer.parseInt(GettersSetters.getSeqIdenficador()) + 1);
                }

            } else {
                if (reenvio) {
                    seqIdentifColeta = String.valueOf(Integer.parseInt(GettersSetters.getSeqIdenficador()) + 1);
                } else {
                    seqIdentifColeta = String.valueOf(db.selectMaxNumIdentificadorColeta(codigoVendedor));
                }
            }
            numeroColeta = stringFormat + seqIdentifColeta; // CÓDIGO DO VENDENDOR COM 4 ULTIMOS CARACTERES + SEQUENCIAL

            if (!numeroColeta.equals("")) {
                GettersSetters.setNumeroColeta(numeroColeta);
                GettersSetters.setSeqIdenficador(seqIdentifColeta);
            } else {
                numeroColeta = "";
                seqIdentifColeta = "";
                stringFormat = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
            numeroColeta = "";
            seqIdentifColeta = "";
            stringFormat = "";
        }

        return numeroColeta;
    }

    /**
     * CRIPTOGRAFIA DE SENHA, USADA NO BD E CRECAP.
     */
    public String cripto(String string) {
        StringBuilder strCripto = new StringBuilder();
        String[] simbolos = new String[4];
        int posicao;

        simbolos[0] = "ABCDEFGHIJLMNOPQRSTUVXZYWK ~!@#$%^&*()Á";
        simbolos[1] = "ÂÀ©Øû×ƒçêùÿ¢Üø£úñÑªº¿®¬¼ëèïÙýÄÅÉæÆôöò»Á";
        simbolos[2] = "abcdefghijlmnopqrstuvxzywk1234567890";
        simbolos[3] = "áâäàåíóÇüé¾¶§÷ÎÏ-+ÌÓß¸°¨·¹³²Õµþîì¡«½";

        if (string.trim().length() > 0) {
            for (int i = 0; i < string.length(); i++) {
                if (simbolos[0].contains(string.substring(i, i + 1))) {
                    posicao = simbolos[0].indexOf(string.substring(i, i + 1));
                    strCripto.append(simbolos[1].substring(posicao, posicao + 1));
                } else if (simbolos[1].contains(string.substring(i, i + 1))) {
                    posicao = simbolos[1].indexOf(string.substring(i, i + 1));
                    strCripto.append(simbolos[3].substring(posicao, posicao + 1));
                } else if (simbolos[2].contains(string.substring(i, i + 1))) {
                    posicao = simbolos[2].indexOf(string.substring(i, i + 1));
                    strCripto.append(simbolos[3].substring(posicao, posicao + 1));
                } else if (simbolos[3].contains(string.substring(i, i + 1))) {
                    posicao = simbolos[3].indexOf(string.substring(i, i + 1));
                    strCripto.append(simbolos[1].substring(posicao, posicao + 1));
                } else {
                    strCripto.append(string.substring(i, i + 1));
                }
            }
        }

        return strCripto.toString();
    }

    public String descripto(String string) {
        StringBuilder strDescripto = new StringBuilder();
        String[] simbolos = new String[4];
        int posicao;

        simbolos[0] = "ABCDEFGHIJLMNOPQRSTUVXZYWK ~!@#$%^&*()Á";
        simbolos[1] = "ÂÀ©Øû×ƒçêùÿ¢Üø£úñÑªº¿®¬¼ëèïÙýÄÅÉæÆôöò»Á";
        simbolos[2] = "abcdefghijlmnopqrstuvxzywk1234567890";
        simbolos[3] = "áâäàåíóÇüé¾¶§÷ÎÏ-+ÌÓß¸°¨·¹³²Õµþîì¡«½";

        for (int i = 0; i < string.length(); i++) {
            if (simbolos[1].contains(string.substring(i, i + 1))) {
                posicao = simbolos[1].indexOf(string.substring(i, i + 1));
                strDescripto.append(simbolos[0].substring(posicao, posicao + 1));
            } else if (simbolos[0].contains(string.substring(i, i + 1))) {
                posicao = simbolos[0].indexOf(string.substring(i, i + 1));
                strDescripto.append(simbolos[1].substring(posicao, posicao + 1));
            } else if (simbolos[3].contains(string.substring(i, i + 1))) {
                posicao = simbolos[3].indexOf(string.substring(i, i + 1));
                strDescripto.append(simbolos[2].substring(posicao, posicao + 1));
            } else if (simbolos[2].contains(string.substring(i, i + 1))) {
                posicao = simbolos[2].indexOf(string.substring(i, i + 1));
                strDescripto.append(simbolos[3].substring(posicao, posicao + 1));
            } else {
                strDescripto.append(string.substring(i, i + 1));
            }
        }

        return strDescripto.toString();
    }

    /* MÉTODO USADO PARA DELETAR ARQUIVOS DENTROS DE PASTAS */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deletaArquivos(File pasta) {
        if (pasta.exists()) {
            if (pasta.isDirectory()) {
                String[] children = pasta.list();
                for (String child : children) {
                    new File(pasta, child).delete();
                }
            }
        }
    }

    public boolean preparaWhatsappEmail(Context context, String vendedor, String nome, String identificador, String data, String filial, String lojaCli,
                                        String codCli, String valor, String numCel, String[] emails, File pdf, String status,
                                        boolean isWhatsApp, boolean isEmail, String vale) {
        ConexaoBDExt conecta = new ConexaoBDExt();
        GeraArquivosColeta geraArquivosColeta = new GeraArquivosColeta();

        boolean enviado = false;
        String dataConv;
        String nomeVend;

        if (pdf == null) {
            if (status.contains("EXT")) {
                pdf = geraArquivosColeta.montaPdfExt(context, nome, identificador, data, filial, lojaCli, codCli, valor, vendedor, status, vale);
            } else {
                pdf = geraArquivosColeta.montaPdf(context, nome, identificador, data, filial, lojaCli, codCli, valor, vendedor);
            }
        }

        if (pdf.length() > 0) {
            if (isEmail) {
                //Log.i("Acessou email:", "teste");
                EnviaEmail enviaEmail = new EnviaEmail("COLETA", filial);
                if (GettersSetters.isIsCfgEmailOk()) {
                    nomeVend = conecta.vendedor("A3_NOME", vendedor);

//            try {
                    //enviaEmail.setFrom("ti@vachileski.com.br"); //email do remetente
                    enviaEmail.setSubject("Coleta Eletrônica de " + nome); //assunto
                    //emails = new String[]{"caina@alba.eti.br".trim(), "ti@vachileski.com.br"}; // para
                    enviaEmail.setTo(emails);

                    conecta.selecionaDadosFilial(filial);

                    String nomeFil = conecta.descricaoFilial;
                    String cnpjFil = conecta.cnpjFilial;
                    String enderFil = conecta.enderecoFilial;
                    String cidadeFil = conecta.cidadeFilial;
                    String cepFil = conecta.cepFilial;
                    String foneFil = conecta.telefoneFilial;

                    if (data.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
                        dataConv = GettersSetters.converteData(data, "BR");
                    } else {
                        dataConv = data;
                    }

                    String body = "<b>Caro(a) " + nome + "</b>,  " + "<br><br>" +
                            "Segue em <b>anexo</b> os dados da Coleta Eletrônica realizada pelo vendedor <b>" + nomeVend.trim() + "</b> no dia <b>" + GettersSetters.converteData(dataConv.substring(0, 4) + "-" + dataConv.substring(4, 6) + "-" + dataConv.substring(6, 8), "EN") + "</b>. <br>" +
                            "Número da coleta: <b>" + identificador + "</b><br><br>";
                    if (nomeFil != null) {
                        if (!foneFil.equals("")) {
                            if (foneFil.substring(1).equals("0")) {
                                foneFil = foneFil.replace(foneFil.substring(1), "");
                            }
                        } else {
                            foneFil = "000000000";
                        }

                        if (!cnpjFil.equals("")) {
                            if (cnpjFil.length() == 14) {
                                cnpjFil = cnpjFil.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                            } else {
                                cnpjFil = cnpjFil.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                            }
                        } else {
                            cnpjFil = "00000000000000";
                        }

                        body += "Empresa Emitente: " + nomeFil + "<br>" +
                                "CNPJ: " + cnpjFil + "<br>" +
                                "Ender.: " + enderFil + "<br>" +
                                "CEP: " + cepFil.replaceAll("([0-9]{5})([0-9]{3})", "$1\\-$2") + " - " + cidadeFil + "<br>" +
                                "Fone.: " + foneFil.replaceAll("([0-9]{2})([0-9]{4})", "($1\\)$2\\-") + "<br><br>" +
                                "Para mais informações entre em contato pelo telefone: " + foneFil.replaceAll("([0-9]{2})([0-9]{4})", "($1\\)$2\\-") + "<br>";
                    }
                    body += "<br><br><br><br>" +
                            "<span style='color:#00008B;Arial;font-size:12px;align:center'><b>Este e-mail foi enviado automaticamente pelo sistema de Coleta Eletrônica do Grupo Vachileski.</b></span>";

                    enviaEmail.setBody(body);
                    try {
                        enviaEmail.addAttachment(pdf.toString(), nome);
                        enviaEmail.setTo(emails);
                        enviado = enviaEmail.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Envio de E-mail");
                    builder.setIcon(R.drawable.error);
                    builder.setCancelable(false);
                    builder.setMessage("Configurações do servidor de e-mails da filial "+ filial +" são inválidas. Entre em contato com o Administrador do sistema.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                    enviado = false;
                }
            } else if (isWhatsApp) {
//            //SALVA PDF EM IMAGEM E GERA ARQUIVO
//            File ROOT_PATH = context.getExternalFilesDir("Arquivos-Whatsapp");
//            File fileRoot = new File(ROOT_PATH + "/" + "img.jpeg");
//
//                Bitmap bitmap = null;
//                ByteArrayOutputStream bmpImgPdf = new ByteArrayOutputStream();
//                try {
//                    PdfRenderer render = new PdfRenderer(ParcelFileDescriptor.open(arquivoPDF, ParcelFileDescriptor.MODE_READ_ONLY));
//                    final int pageCount = render.getPageCount();
//                    for (int i = 0; i < pageCount; i++) {
//                        PdfRenderer.Page page = render.openPage(i);
//                        bitmap = Bitmap.createBitmap(1750, 2500, Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(bitmap);
//                        canvas.drawColor(Color.WHITE);
//                        canvas.drawBitmap(bitmap, 0, 0, null);
//                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                        page.close();
//                    }
//                    render.close();
//
//                    assert bitmap != null;
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bmpImgPdf);
//                    FileOutputStream saidaFotoComprimida = new FileOutputStream(fileRoot);
//                    saidaFotoComprimida.write(bmpImgPdf.toByteArray());
//                    saidaFotoComprimida.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                conecta.ConnectToDatabase("A", GettersSetters.getConexaoBD());
//                vendedor = conecta.usuario("NOME", GettersSetters.getCodigoVendedor());

                Intent sendIntent = new Intent();
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", pdf);
                } else {
                    uri = Uri.fromFile(pdf);
                }

                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.putExtra("jid", numCel.replace("+", "").replace(" ", "") + "@s.whatsapp.net");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("*/*");

                AlertDialog.Builder builderFilial = new AlertDialog.Builder(context);
                builderFilial.setTitle("Whatsapp");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(false);
                builderFilial.setMessage("Aplicativo WhatsApp não instalado!");
                builderFilial.setPositiveButton("OK", null);

                try {
                    sendIntent.setPackage(null);
                    if (context.getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
                        sendIntent.setPackage("com.whatsapp");
                    } else if (context.getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b") != null) {
                        sendIntent.setPackage("com.whatsapp.w4b"); //WPP BUSINESS
                    }

                    if (sendIntent.getPackage() != null) {
                        context.startActivity(sendIntent);
                        enviado = true;
                    } else {
                        enviado = false;

                        AlertDialog dialog = builderFilial.create();
                        dialog.show();
                    }
                } catch (RuntimeException e) {
                    enviado = false;

                    AlertDialog dialog = builderFilial.create();
                    dialog.show();
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("PDF");
            builder.setIcon(R.drawable.error);
            builder.setCancelable(false);
            builder.setMessage("PDF não gerado!");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return enviado;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void clearCache(Context context) {
        File[] files = context.getCacheDir().listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    public static boolean validaData(String data) {
        String pattern = "dd/MM/yyyy";
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false); // COMO FALSE GERA ERRO DE DATA

        try {
            sdf.parse(data); // DATA FORMATADA CORRETAMENTE
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
