package pneus.vachileski_mobi_funcoes_genericas;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.util.Base64;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import pneus.vachileski_mobi_apanhe_pneus.R;

@SuppressWarnings({"DanglingJavadoc", "IntegerDivisionInFloatingPointContext", "ResultOfMethodCallIgnored"})
public class GeraArquivosColeta {
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    ConexaoBDExt conecta = new ConexaoBDExt();

    /**
     * PDF INTERNO
     **/
    public File montaPdf(Context context, String nome, String numeroColeta, String data, String filial, String lojaCli, String codCli, String valor, String codVend) {
        int numeroPg = 1;
        int i;
        int numMaxIt = 15;
        int proxIt = 0;
        int contItPPg = 0;
        int linha;

        String dataConv;
        String vendedor;
        String borracheiro;
        String nomeBorr;
        String email;
        String telefone;
        String documentoConvertido;
        String inscrEstadual;
        String endereco;
        String enderecoCobr;
        String condPgto;
        String formaPgto;
        String estado;
        String municipio;
        String cep;
        String dataChegada;
        String infoAdicional;
        String orcamento;

        ConexaoBDInt db = new ConexaoBDInt(context);
        PdfDocument pdfDocument = new PdfDocument();
        Cursor cursorDadosItens;

        File ROOT_PATH = context.getExternalFilesDir("Arquivos-Whatsapp"); //Environment.getExternalStorageDirectory().getPath() + "/VachileskiMobi/";
//        File fileRoot;
////        File file = null;
        String FILE_NAME = nome.replace("/", "").replace("-", " ") + "-APANHE DE PNEUS.pdf";

        if (ROOT_PATH.exists()) {
            FuncoesGenericas.deletaArquivos(ROOT_PATH);
        } else {
            ROOT_PATH.mkdirs();
        }

        if (data.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
            dataConv = GettersSetters.converteData(data, "BR");
        } else {
            dataConv = data;
        }

        String queryBuscaItens = "SELECT * " +
                "FROM COLETA_ITENS " +
                "WHERE 1 = 1 " +
                "  AND trim(COL_IT_FILIAL) = '" + filial.trim() + "' " +
                "  AND trim(COL_IT_IDENTIF) = '" + numeroColeta.trim() + "' " +
                "  AND trim(COL_IT_DATA) = '" + dataConv.trim() + "' " +
                "  AND trim(COL_IT_COD_CLI) = '" + codCli.trim() + "' " +
                "  AND trim(COL_IT_LOJA_CLI) = '" + lojaCli.trim() + "' " +
                "ORDER BY CAST(COL_IT_ITEM AS NUMERIC)";

        cursorDadosItens = db.buscaColetaItens(queryBuscaItens);
        if (cursorDadosItens.getCount() > 0) {
            if (cursorDadosItens.getCount() > numMaxIt) {
                if ((cursorDadosItens.getCount() % numMaxIt) == 0) {
                    numeroPg = cursorDadosItens.getCount() / numMaxIt;
                } else {
                    numeroPg = (cursorDadosItens.getCount() / numMaxIt + 1);
                }
            }

            for (i = 1; i <= numeroPg; i++) {
                ROOT_PATH = context.getExternalFilesDir("Arquivos-Whatsapp"); //Environment.getExternalStorageDirectory().getPath() + "/VachileskiMobi/";
//                fileRoot = new File(String.valueOf(ROOT_PATH));
                int pageWidth = 1750; //LARGURA
                int pageHeigth = 2500; //ALTURA
                Resources res = context.getResources();
                int idLogo;

                Paint dadosFilial = new Paint();
                Paint imagens = new Paint();
                Paint cabecalho = new Paint();
                Paint corpoEsquerda = new Paint();
                Paint corpoEsquerdaBold = new Paint();
                Paint corpoDireita = new Paint();
                Paint corpoDireitaBold = new Paint();
                Paint valorTotal = new Paint();
                Paint tabelaItens = new Paint();
                Paint termos = new Paint();
                Paint tabelaItensDesenho = new Paint();

                tabelaItensDesenho.setTextSize(12);

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigth, i).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                vendedor = conecta.vendedor("A3_NOME", codVend);

                db.buscaColetasCabec(data, numeroColeta, nome, 0, filial, 1, context);
                infoAdicional = db.buscaColetaInfoAdic;
                dataChegada = db.buscaColetaDtChegada;
                orcamento = (db.buscaColetaOrcamento.equals("T") ? "COM" : "SEM") + " ORÇAMENTO.";

                if (!db.buscaColetaNomeBorr.equals("")) {
                    nomeBorr = db.buscaColetaNomeBorr;
                    String documento = db.buscaColetaDocBorr.replaceAll("\\D", "");
                    if (documento.length() == 14) {
                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                    } else {
                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                    }
                    borracheiro = documento + " | " + nomeBorr;
                } else {
                    borracheiro = "SEM BORRACHEIRO";
                }

                /**
                 * BUSCA NOVO CLIENTE E CABEÇALHO *
                 * **/
                String qryBuscaCliColeta = "SELECT COL_CLI_NOME " +
                        "FROM COLETA_CLI " +
                        "WHERE trim(COL_CLI_FILIAL) = '" + filial.trim() + "'" +
                        "  AND trim(COL_CLI_IDENTIF) = '" + numeroColeta.trim() + "'" +
                        "  AND trim(COL_CLI_DATA) = '" + dataConv.trim() + "'" +
                        "  AND trim(COL_CLI_COD_CLI) = '" + codCli.trim() + "'" +
                        "  AND trim(COL_CLI_LOJA_CLI) = '" + lojaCli.trim() + "'";

                //System.out.println(qryBuscaCliColeta);

                Cursor cursor = db.buscaColetaCli(qryBuscaCliColeta, true);
                if (cursor.getCount() > 0) {
                    String documento = db.novoClienteColeta("COL_CLI_DOC_CLI", codCli, lojaCli, false, numeroColeta).replaceAll("\\D", "");
                    if (documento.length() == 14) {
                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                    } else {
                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                    }

                    email = db.novoClienteColeta("COL_CLI_EMAIL", codCli, lojaCli, false, numeroColeta).trim();
                    telefone = db.novoClienteColeta("COL_CLI_ENT_FONE", codCli, lojaCli, false, numeroColeta);
                    documentoConvertido = documento;
                    inscrEstadual = db.novoClienteColeta("COL_CLI_IE", codCli, lojaCli, false, numeroColeta);
                    endereco = db.novoClienteColeta("COL_CLI_ENT_END", codCli, lojaCli, false, numeroColeta);
                    estado = db.novoClienteColeta("COL_CLI_ENT_EST", codCli, lojaCli, false, numeroColeta);
                    municipio = db.novoClienteColeta("COL_CLI_ENT_MUNIC", codCli, lojaCli, false, numeroColeta);
                    cep = db.novoClienteColeta("COL_CLI_ENT_CEP", codCli, lojaCli, false, numeroColeta);
                    enderecoCobr = db.novoClienteColeta("COL_CLI_COBR_END", codCli, lojaCli, false, numeroColeta);
                } else {
                    db.buscaClientesColeta("", "","", new ArrayList<>(0), codCli, lojaCli);
                    String documento = db.cgcCliente;
                    if (documento.length() == 14) {
                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                    } else {
                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                    }
                    email = db.emailCli;
                    telefone = db.foneCli;
                    documentoConvertido = documento;
                    inscrEstadual = db.ieCli;
                    endereco = db.endCli;
                    estado = db.ufCli;
                    municipio = db.munCli;
                    cep = db.cepCli;
                    enderecoCobr = db.endCobCli;

                    //conecta.ConnectToDatabase("P", GettersSetters.getConexaoBD());
//                    String documento = conecta.cliente("A1_CGC", codCli, lojaCli).replaceAll("\\D", "");
//                    if (documento.length() == 14) {
//                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
//                    } else {
//                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
//                    }
//                    email = conecta.cliente("A1_EMAIL", codCli, lojaCli).trim();
//                    telefone = conecta.cliente("A1_TEL", codCli, lojaCli);
//                    documentoConvertido = documento;
//                    inscrEstadual = conecta.cliente("A1_INSCR", codCli, lojaCli);
//                    endereco = conecta.cliente("A1_END", codCli, lojaCli);
//                    estado = conecta.cliente("A1_EST", codCli, lojaCli);
//                    municipio = conecta.cliente("A1_MUN", codCli, lojaCli);
//                    cep = conecta.cliente("A1_CEP", codCli, lojaCli);
//                    enderecoCobr = conecta.cliente("A1_ENDCOB", codCli, lojaCli);
                }

                /** MASCARA DO CEP **/
                cep = cep.replaceAll("([0-9]{5})([0-9]{3})", "$1\\-$2");

                condPgto = (db.buscaPagamento("COL_CABEC_COND_PG", codCli, lojaCli, numeroColeta, data)).equals("0") ? "À Vista" : db.buscaPagamento("COL_CABEC_COND_PG", codCli, lojaCli, numeroColeta, data) + " dias";
                formaPgto = db.buscaPagamento("COL_CABEC_FORMA_PG", codCli, lojaCli, numeroColeta, data);

                conecta.selecionaDadosFilial(filial);
                String nomeFil = conecta.descricaoFilial;
                String cnpjFil = conecta.cnpjFilial;
                String enderFil = conecta.enderecoFilial;
                String cidadeFil = conecta.cidadeFilial;
                String cepFil = conecta.cepFilial;
                String foneFil = conecta.telefoneFilial;

                if (filial.contains("0401")) {
                    idLogo = R.drawable.logovb;
                } else if (filial.contains("0801")) {
                    idLogo = R.drawable.logovlk;
                } else if (filial.contains("0901")) {
                    idLogo = R.drawable.logovp;
                } else if (filial.contains("1001")) {
                    idLogo = R.drawable.logogv;
                } else if (filial.contains("1101") || filial.contains("1201")) {
                    idLogo = R.drawable.logoost;
                } else if (filial.contains("1301") || filial.contains("1401")) {
                    idLogo = R.drawable.logolauxvach;
                } else {
                    idLogo = R.drawable.logovachileski;
                }

                Bitmap bmpLogo = BitmapFactory.decodeResource(res, idLogo);
                Bitmap escalaLogo = Bitmap.createScaledBitmap(bmpLogo, 798, 262, false);

                File DIRECTORY = context.getExternalFilesDir("Assinaturas");
                ///Environment.getExternalStorageDirectory().getAbsolutePath()+"/VachileskiMobi/Assinaturas/"; //Environment.getDataDirectory().getAbsolutePath() + "/VachileskiMobi/Assinaturas/";
                String pic_nameCli, pic_nameBorr, storedPathCli, storedPathBorr;

//                if (fileRoot.exists()) {
//                    //noinspection ResultOfMethodCallIgnored
//                    fileRoot.delete();
//                }
//
//                if (!fileRoot.exists()) {
//                    //noinspection ResultOfMethodCallIgnored
//                    fileRoot.mkdirs();
//                }

                if (nomeFil != null) {
                    if (!foneFil.trim().equals("")) {
                        if (foneFil.substring(1).equals("0")) {
                            foneFil = foneFil.replace(foneFil.substring(1), "");
                        }
                    }

                    if (cnpjFil.trim().equals("")) {
                        if (cnpjFil.length() == 14) {
                            cnpjFil = cnpjFil.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                        } else {
                            cnpjFil = cnpjFil.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                        }
                    }

                    /** DADOS DA FILIAL DA COLETA **/
                    dadosFilial.setTextAlign(Paint.Align.LEFT);
                    dadosFilial.setTextSize(20);
                    dadosFilial.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                    canvas.drawText(nomeFil, 50, 60, dadosFilial); //RAZAO SOCIAL
                    canvas.drawText("CNPJ: " + cnpjFil, 50, 90, dadosFilial); //CNPJ
                    canvas.drawText("Ender.: " + enderFil, 50, 120, dadosFilial); //ENDEREÇO
                    canvas.drawText("CEP: " + cepFil.replaceAll("([0-9]{5})([0-9]{3})", "$1\\-$2") + " - " + cidadeFil, 50, 150, dadosFilial); //CEP CIDADE E ESTADO
                    canvas.drawText("Fone.: " + foneFil.replaceAll("([0-9]{2})([0-9]{4})", "($1\\)$2\\-"), 50, 180, dadosFilial); //TELEFONE

                    /** LOGO **/
                    canvas.drawBitmap(escalaLogo, (pageWidth / 2), 10, imagens); //INICIA NO CENTRO DA PG
                } else {
                    canvas.drawBitmap(escalaLogo, ((pageWidth / 2) - (escalaLogo.getWidth() / 2)), 10, imagens); //CENTRO DA PG DE ACORDO COM A IMG
                }

                /** ABAIXO DO LOGO **/
                cabecalho.setTextAlign(Paint.Align.LEFT);
                cabecalho.setTextSize(20);
                cabecalho.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                canvas.drawText("Caro cliente,", 50, 250, cabecalho);
                canvas.drawText("Segue abaixo, os dados da Coleta Eletrônica realizada pelo vendedor " + vendedor.trim() + ".", 50, 310, cabecalho);

                cabecalho.setColor(res.getColor(R.color.red));
                canvas.drawText("Data da Coleta: " + GettersSetters.converteData(dataConv.substring(0, 4) + "-" + dataConv.substring(4, 6) + "-" + dataConv.substring(6, 8), "EN") + " às " + db.buscaColetaHora, 50, 340, cabecalho);

                if (!dataChegada.trim().equals("") && dataChegada != null) {
                    canvas.drawText("Data prevista da chegada dos Pneus na Filial: " + dataChegada, 50, 370, cabecalho);
                }

                cabecalho.setColor(res.getColor(R.color.colorAccent));
                canvas.drawText("Número da Coleta Eletrônica: " + numeroColeta, 50, 405, cabecalho);

                /** CÓDIGO DE BARRAS **/
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(filial + numeroColeta, BarcodeFormat.CODE_128, 260, 75);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmapBarcode = barcodeEncoder.createBitmap(bitMatrix);
                    //imageView.setImageBitmap(bitmap);
                    canvas.drawBitmap(bitmapBarcode, 1380, 300, imagens);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                /**
                 * IDENTIFICAÇÃO DAS PAGINAÇÃO
                 * **/
                cabecalho.setColor(res.getColor(R.color.blue));
                cabecalho.setTextSize(14);
                canvas.drawText("Página " + i + "/" + numeroPg, 1585, 405, cabecalho);

                corpoEsquerda.setTextAlign(Paint.Align.LEFT);
                corpoEsquerda.setTextSize(20);
                corpoEsquerda.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                corpoEsquerdaBold.setTextAlign(Paint.Align.LEFT);
                corpoEsquerdaBold.setTextSize(20);
                corpoEsquerdaBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                corpoEsquerdaBold.setColor(res.getColor(R.color.blue));

                canvas.drawLine(50, 425, pageWidth - 50, 425, corpoEsquerda);
                if (!filial.contains("1101") && !filial.contains("1201")) {
                    canvas.drawLine(50, 425, 50, 825, corpoEsquerda);
                    canvas.drawLine((pageWidth / 2) - 120, 425, (pageWidth / 2) - 120, 825, corpoEsquerda); //LINHA MEIO TABELA
                } else {
                    canvas.drawLine(50, 425, 50, 785, corpoEsquerda);
                    canvas.drawLine((pageWidth / 2) - 120, 425, (pageWidth / 2) - 120, 785, corpoEsquerda); //LINHA MEIO TABELA
                }
                canvas.drawText("Razão Social", 60, 450, corpoEsquerda);
                canvas.drawLine(50, 465, pageWidth - 50, 465, corpoEsquerda);
                canvas.drawText("CPF/CNPJ", 60, 490, corpoEsquerda);
                canvas.drawLine(50, 505, pageWidth - 50, 505, corpoEsquerda);
                canvas.drawText("Inscr. Estadual", 60, 530, corpoEsquerda);
                canvas.drawLine(50, 545, pageWidth - 50, 545, corpoEsquerda);
                canvas.drawText("Endereço", 60, 570, corpoEsquerda);
                canvas.drawLine(50, 585, pageWidth - 50, 585, corpoEsquerda);
                canvas.drawText("E-mail", 60, 610, corpoEsquerda);
                canvas.drawLine(50, 625, pageWidth - 50, 625, corpoEsquerda);
                canvas.drawText("Telefone", 60, 650, corpoEsquerda);
                canvas.drawLine(50, 665, pageWidth - 50, 665, corpoEsquerda);
                canvas.drawText("Endereço Cobrança", 60, 690, corpoEsquerda);
                canvas.drawLine(50, 705, pageWidth - 50, 705, corpoEsquerda);
                canvas.drawText("Condição de Pagamento", 60, 730, corpoEsquerda);
                canvas.drawLine(50, 745, pageWidth - 50, 745, corpoEsquerda);
                canvas.drawText("Informações Adicionais", 60, 770, corpoEsquerdaBold);
                canvas.drawLine(50, 785, pageWidth - 50, 785, corpoEsquerda);
                /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                if (!filial.contains("1101") && !filial.contains("1201")) {
                    canvas.drawText("Borracheiro", 60, 810, corpoEsquerda);
                    canvas.drawLine(50, 825, pageWidth - 50, 825, corpoEsquerda);
                    canvas.drawLine(pageWidth - 50, 425, pageWidth - 50, 825, corpoEsquerda);
                } else {
                    canvas.drawLine(pageWidth - 50, 425, pageWidth - 50, 785, corpoEsquerda);
                }

                corpoDireitaBold.setTextAlign(Paint.Align.LEFT);
                corpoDireitaBold.setTextSize(20);
                corpoDireitaBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                corpoDireitaBold.setColor(res.getColor(R.color.blue));

                corpoDireita.setTextSize(20);
                canvas.drawText(nome, (pageWidth / 2) - 50, 450, corpoDireita);
                canvas.drawText(documentoConvertido, (pageWidth / 2) - 50, 490, corpoDireita);
                canvas.drawText(inscrEstadual, (pageWidth / 2) - 50, 530, corpoDireita);
                canvas.drawText(endereco.trim() + " | " + municipio.trim() + "/" + estado.trim() + " " + cep.trim(), (pageWidth / 2) - 50, 570, corpoDireita);
                canvas.drawText(email, (pageWidth / 2) - 50, 610, corpoDireita);
                canvas.drawText(telefone, (pageWidth / 2) - 50, 650, corpoDireita);
                canvas.drawText(enderecoCobr, (pageWidth / 2) - 50, 690, corpoDireita);
                canvas.drawText(condPgto + " | " + formaPgto, (pageWidth / 2) - 50, 730, corpoDireita);
                if (infoAdicional.trim().length() > 70) {
                    corpoDireitaBold.setTextSize(16);
                    canvas.drawText(infoAdicional.substring(0, 69), (pageWidth / 2) - 50, 763, corpoDireitaBold);
                    canvas.drawText(infoAdicional.substring(69) + " | -> " + orcamento, (pageWidth / 2) - 50, 780, corpoDireitaBold);
                } else {
                    canvas.drawText(infoAdicional + " | -> " + orcamento, (pageWidth / 2) - 50, 770, corpoDireitaBold);
                }
                /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                if (!filial.contains("1101") && !filial.contains("1201")) {
                    canvas.drawText(borracheiro, (pageWidth / 2) - 50, 810, corpoDireita);
                }

                /** VALOR TOTAL **/
                valorTotal.setTextAlign(Paint.Align.LEFT);
                valorTotal.setTextSize(30);
                valorTotal.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                valorTotal.setColor(res.getColor(R.color.red));
                canvas.drawText("Valor Total* R$: " + valor, 60, 900, valorTotal);

                /** CABEÇALHO DA TABELA **/
                canvas.drawLine(50, 950, pageWidth - 50, 950, corpoEsquerda); //LINHA SUPERIOR
                canvas.drawLine(50, 950, 50, 1000, corpoEsquerda); //LINHA ESQUERDA
                canvas.drawText("Item", 53, 975, corpoEsquerda);
                canvas.drawLine(110, 950, 110, 1000, corpoEsquerda); //LINHA ITEM X QTDE
                canvas.drawText("Qtde", 113, 975, corpoEsquerda);
                canvas.drawLine(170, 950, 170, 1000, corpoEsquerda); //LINHA QTDE X BITOLA
                canvas.drawText("Bitola", 354, 975, corpoEsquerda);
                canvas.drawLine(520, 950, 520, 1000, corpoEsquerda); //LINHA BITOLA X MARCA
                canvas.drawText("Marca", 535, 975, corpoEsquerda);
                canvas.drawLine(637, 950, 637, 1000, corpoEsquerda); //LINHA MARCA X MODELO
                canvas.drawText("Modelo", 640, 975, corpoEsquerda);
                canvas.drawLine(754, 950, 754, 1000, corpoEsquerda); //LINHA MODELO X SERIE
                canvas.drawText("Série", 770, 975, corpoEsquerda);
                canvas.drawLine(871, 950, 871, 1000, corpoEsquerda); //LINHA SERIE X MONTADO
                canvas.drawText("DOT", 885, 975, corpoEsquerda);
                canvas.drawLine(988, 950, 988, 1000, corpoEsquerda); //LINHA MONTADO X DOT
                canvas.drawText("Montado?", 991, 975, corpoEsquerda);
                canvas.drawLine(1105, 950, 1105, 1000, corpoEsquerda); //LINHA DOT X URGENTE
                canvas.drawText("Urgente", 1108, 975, corpoEsquerda);
                canvas.drawLine(1222, 950, 1222, 1000, corpoEsquerda); //LINHA URGENTE X DESENHO
                canvas.drawText("Desenho", 1225, 975, corpoEsquerda);
                canvas.drawLine(1339, 950, 1339, 1000, corpoEsquerda); //LINHA DESENHO X OBSERVACAO
                canvas.drawText("Observação", 1355, 975, corpoEsquerda);
                canvas.drawLine(1590, 950, 1590, 1000, corpoEsquerda); //LINHA OBSERVACAO X VALOR
                canvas.drawText("Valor Total", 1592, 975, corpoEsquerda);
                canvas.drawLine(pageWidth - 50, 950, pageWidth - 50, 1000, corpoEsquerda); //LINHA DIREITA
                canvas.drawLine(50, 1000, pageWidth - 50, 1000, corpoEsquerda); //LINHA INFERIOR

                int linhaIni = 1025; // somar 30 nas próximas linhas

                tabelaItens.setTextSize(15);
                for (int j = proxIt; j < db.arrBuscaColetaItFilial.size(); j++) {
                    canvas.drawText(db.arrBuscaColetaItItem.get(j), 70, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItQtd.get(j), 125, linhaIni, tabelaItens);
                    if (db.arrBuscaColetaItBitola.get(j).trim().length() > 42) {
                        canvas.drawText(db.arrBuscaColetaItBitola.get(j).substring(0, 41), 173, linhaIni, tabelaItens);
                        linhaIni += 15;
                        canvas.drawText(db.arrBuscaColetaItBitola.get(j).substring(41), 173, linhaIni, tabelaItens);
                    } else {
                        canvas.drawText(db.arrBuscaColetaItBitola.get(j), 173, linhaIni, tabelaItens);
                    }
                    canvas.drawText(db.arrBuscaColetaItMarca.get(j), 527, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItModelo.get(j), 640, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItSerie.get(j), 757, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItDot.get(j), 874, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItMontado.get(j).equals("0") ? "Montado" : "", 991, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItUrgente.get(j).equals("1") ? "Urgente" : "", 1108, linhaIni, tabelaItens);
                    canvas.drawText(db.arrBuscaColetaItDesenho.get(j), 1225, linhaIni, tabelaItensDesenho);

                    if (db.arrBuscaColetaItObs.get(j).trim().length() > 29) {
                        String infoAdic = String.format("%-100.100s", db.arrBuscaColetaItObs.get(j).trim()); //ADICIONA 100 POSICOES PARA NÃO DAR EXCEÇÃO

                        Paint tabelaItensQuebra = new Paint();
                        tabelaItensQuebra.setTextSize(13);
                        canvas.drawText(infoAdic.substring(0, 29).trim(), 1344, linhaIni, tabelaItensQuebra);
                        linhaIni += 15;
                        canvas.drawText(infoAdic.substring(29, 57).trim(), 1344, linhaIni, tabelaItensQuebra);
                        linhaIni += 15;
                        canvas.drawText(infoAdic.substring(57, 86).trim(), 1344, linhaIni, tabelaItensQuebra);
                        linhaIni += 15;
                        canvas.drawText(infoAdic.substring(86).trim(), 1344, linhaIni, tabelaItensQuebra);
                    } else {
                        canvas.drawText(db.arrBuscaColetaItObs.get(j), 1344, linhaIni, tabelaItens);
                    }

                    canvas.drawText(df.format(Double.parseDouble(db.arrBuscaColetaItVlrTotal.get(j))), 1596, linhaIni, tabelaItens);
                    canvas.drawLine(50, linhaIni + 10, pageWidth - 50, linhaIni + 10, corpoEsquerda); //LINHA INFERIOR
                    linhaIni += 30;

                    contItPPg++;
                    if (contItPPg == numMaxIt) {
                        proxIt = j + 1;
                        contItPPg = 0;
                        break;
                    }
                }

                /** DIVISÓRIAS **/
                canvas.drawLine(50, 1000, 50, linhaIni - 20, corpoEsquerda); //LINHA ESQUERDA
                canvas.drawLine(110, 1000, 110, linhaIni - 20, corpoEsquerda); //LINHA ITEM X QTDE
                canvas.drawLine(170, 1000, 170, linhaIni - 20, corpoEsquerda); //LINHA QTDE X BITOLA
                canvas.drawLine(520, 1000, 520, linhaIni - 20, corpoEsquerda); //LINHA BITOLA X MARCA
                canvas.drawLine(637, 1000, 637, linhaIni - 20, corpoEsquerda); //LINHA MARCA X MODELO
                canvas.drawLine(754, 1000, 754, linhaIni - 20, corpoEsquerda); //LINHA MODELO X SERIE
                canvas.drawLine(871, 1000, 871, linhaIni - 20, corpoEsquerda); //LINHA SERIE X MONTADO
                canvas.drawLine(988, 1000, 988, linhaIni - 20, corpoEsquerda); //LINHA MONTADO X DOT
                canvas.drawLine(1105, 1000, 1105, linhaIni - 20, corpoEsquerda); //LINHA DOT X URGENTE
                canvas.drawLine(1222, 1000, 1222, linhaIni - 20, corpoEsquerda); //LINHA URGENTE X DESENHO
                canvas.drawLine(1339, 1000, 1339, linhaIni - 20, corpoEsquerda); //LINHA DESENHO X OBSERVACAO
                canvas.drawLine(1590, 1000, 1590, linhaIni - 20, corpoEsquerda); //LINHA OBSERVACAO X VALOR
                canvas.drawLine(pageWidth - 50, 1000, pageWidth - 50, linhaIni - 20, corpoEsquerda); //LINHA DIREITA

                /** ASSINATURAS **/
                /** CLIENTE **/
                byte[] blobCli = db.buscaAssinaturas("COL_CABEC_ASSIN_CLI", codCli, lojaCli, numeroColeta, dataConv);
                pic_nameCli = "ASSINATURA-CLIENTE.png";
                storedPathCli = DIRECTORY + "/" + pic_nameCli;
                /** DELETA ARQUIVOS PNG */
                File fileAssinaturaCli = new File(DIRECTORY, pic_nameCli);
                //noinspection ResultOfMethodCallIgnored
                fileAssinaturaCli.delete();
                /****/

                /** CONVERTE ASSINATURAS DE BYTE PARA PNG **/
                //byte[] dataBmpAssinCli = Base64.decode(blobCli, Base64.DEFAULT);
                Bitmap bitmapAssinaturaCli = BitmapFactory.decodeByteArray(blobCli, 0, blobCli.length);
                bitmapAssinaturaCli = Bitmap.createScaledBitmap(bitmapAssinaturaCli, 794, 420, true);
                ImageView image = new ImageView(context);
                image.setImageBitmap(bitmapAssinaturaCli);

                try {
                    FileOutputStream mFileOutStream = new FileOutputStream(storedPathCli);
                    bitmapAssinaturaCli.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                    mFileOutStream.flush();
                    mFileOutStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (borracheiro.equals("SEM BORRACHEIRO")) {
                    canvas.drawBitmap(bitmapAssinaturaCli, (pageWidth / 2) - (bitmapAssinaturaCli.getWidth() / 2), linhaIni, imagens);
                    //canvas.drawText(nome, (bitmapAssinaturaCli.getWidth() / 2) - 40, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                    canvas.drawText("ASSINATURA CLIENTE", (pageWidth / 2) - 100, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                } else {
                    canvas.drawBitmap(bitmapAssinaturaCli, 50, linhaIni, imagens);
                    canvas.drawText("ASSINATURA CLIENTE", (bitmapAssinaturaCli.getWidth() / 2) - 40, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                }

                /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                if (!filial.contains("1101") && !filial.contains("1201")) {
                    /** BORRACHEIRO **/
                    if (!borracheiro.equals("SEM BORRACHEIRO")) {
                        byte[] blobBorr = db.buscaAssinaturas("COL_CABEC_ASSIN_BORR", codCli, lojaCli, numeroColeta, dataConv);
                        if (blobBorr != null) {
                            pic_nameBorr = "ASSINATURA-BORRACH.png";
                            storedPathBorr = DIRECTORY + "/" + pic_nameBorr;
                            /** DELETA ARQUIVOS PNG */
                            File fileAssinaturaBorr = new File(DIRECTORY, pic_nameBorr);
                            //noinspection ResultOfMethodCallIgnored
                            fileAssinaturaBorr.delete();
                            /****/

                            /** CONVERTE ASSINATURAS DE BYTE PARA PNG **/
                            //byte[] dataBmpAssinBorr = Base64.decode(blobBorr, Base64.DEFAULT);
                            Bitmap bitmapAssinaturaBorr = BitmapFactory.decodeByteArray(blobBorr, 0, blobBorr.length);
                            bitmapAssinaturaBorr = Bitmap.createScaledBitmap(bitmapAssinaturaBorr, 794, 420, true);

                            ImageView imageBorr = new ImageView(context);
                            imageBorr.setImageBitmap(bitmapAssinaturaBorr);

                            try {
                                FileOutputStream mFileOutStream = new FileOutputStream(storedPathBorr);
                                bitmapAssinaturaBorr.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                                mFileOutStream.flush();
                                mFileOutStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            canvas.drawBitmap(bitmapAssinaturaBorr, bitmapAssinaturaCli.getWidth() + 110, linhaIni, imagens);
                            //canvas.drawText(nomeBorr, (bitmapAssinaturaCli.getWidth() + bitmapAssinaturaBorr.getWidth() / 2) + 20, linhaIni + bitmapAssinaturaBorr.getHeight() + 20, corpoEsquerda);
                            canvas.drawText("ASSINATURA BORRACHEIRO", (bitmapAssinaturaCli.getWidth() + bitmapAssinaturaBorr.getWidth() / 2) + 20, linhaIni + bitmapAssinaturaBorr.getHeight() + 20, corpoEsquerda);
                        }
                    }
                }

                if (bitmapAssinaturaCli != null) {
                    linha = linhaIni + bitmapAssinaturaCli.getHeight() + 100;
                } else {
                    linha = linhaIni + 520;
                }

                canvas.drawLine(50, linha - 30, pageWidth - 50, linha - 30, corpoEsquerda);
                corpoEsquerda.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("TERMOS", pageWidth / 2, linha, corpoEsquerda);
                termos.setTextSize(20);
                canvas.drawText(res.getString(R.string.termo1).substring(1, 154), 50, linha + 50, termos);
                canvas.drawText(res.getString(R.string.termo1).substring(155, 309), 50, linha + 75, termos);
                canvas.drawText(res.getString(R.string.termo1).substring(310), 50, linha + 100, termos);

                canvas.drawText(res.getString(R.string.termo2).substring(1, 141).replace("[NOME_EMPRESA]",nomeFil.trim().toUpperCase()), 50, linha + 135, termos);
                canvas.drawText(res.getString(R.string.termo2).substring(142).replace("[NOME_EMPRESA]",nomeFil.trim().toUpperCase()), 50, linha + 160, termos);

                termos.setColor(res.getColor(R.color.red));
                canvas.drawText("* " + res.getString(R.string.termo3), 50, linha + 195, termos);

                corpoEsquerda.setColor(res.getColor(R.color.blue));
                corpoEsquerda.setTextSize(18);
                corpoEsquerda.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                corpoEsquerda.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Este arquivo foi gerado automaticamente pelo sistema de Coleta Eletrônica do Grupo Vachileski", pageWidth / 2, pageHeigth - 50, corpoEsquerda);

                pdfDocument.finishPage(page);

                /** -- FIM DA PÁGINA -- **/
            }
            FuncoesGenericas.deletaArquivos(ROOT_PATH);
        }

        return geraArqPdf(pdfDocument, ROOT_PATH, FILE_NAME);
    }

    /**
     * GERA PDF EXTERNO
     **/
    public File montaPdfExt(Context context, String nome, String numeroColeta, String data, String filial, String lojaCli, String codCli, String valor, String codVendedor, String status, String vale) {
        int numeroPg = 1;
        int i;
        int numMaxIt = 15;
        int proxIt = 0;
        int contItPPg = 0;
        int contaDadosRs = 0;
        int pageWidth = 1750; //LARGURA
        int pageHeigth = 2500; //ALTURA
        int idLogo;
        int linha;

        String infoAdicional = "";
        String dataConv;
        String vendedor = "";
        String borracheiro = "";
        String nomeBorr;
        String email = "";
        String telefone = "";
        String inscrEstadual = "";
        String endereco = "";
        String enderecoCobr = "";
        String condPgto = "";
        String formaPgto = "";
        String estado = "";
        String municipio = "";
        String cep = "";
        String documento = "";
        String dataChegada = "";
        String horaColeta = "";
        String orcamento = "";
        String coletadoPor = "";

        ConexaoBDInt db = new ConexaoBDInt(context);
        PdfDocument pdfDocument = new PdfDocument();
        ResultSet rsDados = null;
        Resources res = context.getResources();

        ArrayList<String> arrBuscaColetaItFilial = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItItem = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItQtd = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItBitola = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItMarca = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItModelo = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItSerie = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItDot = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItMontado = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItUrgente = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItDesenho = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItObs = new ArrayList<>();
        ArrayList<String> arrBuscaColetaItVlrTotal = new ArrayList<>();

        File ROOT_PATH = context.getExternalFilesDir("Arquivos-Whatsapp"); //Environment.getExternalStorageDirectory().getPath() + "/VachileskiMobi/";
//        File fileRoot = new File(String.valueOf(ROOT_PATH));
        String FILE_NAME = nome.replace("/", "").replace("-", " ") + "-APANHE DE PNEUS.pdf";

        if (ROOT_PATH.exists()) {
            FuncoesGenericas.deletaArquivos(ROOT_PATH);
        } else {
            ROOT_PATH.mkdirs();
        }

        if (data.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
            dataConv = GettersSetters.converteData(data, "BR");
        } else {
            dataConv = data;
        }

        try {
            rsDados = conecta.buscaColetaExtItens(filial, numeroColeta, data, codCli, lojaCli);
            rsDados.last();
            contaDadosRs = rsDados.getRow();
            rsDados.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            contaDadosRs = 0;
        } finally {
            try {
                while (rsDados.next()) {
                    arrBuscaColetaItFilial.add(rsDados.getString(1));
                    arrBuscaColetaItItem.add(rsDados.getString(3));
                    arrBuscaColetaItQtd.add(rsDados.getString(7));
                    arrBuscaColetaItBitola.add(rsDados.getString(11));
                    arrBuscaColetaItMarca.add(rsDados.getString(12));
                    arrBuscaColetaItModelo.add(rsDados.getString(13));
                    arrBuscaColetaItSerie.add(rsDados.getString(14));
                    arrBuscaColetaItDot.add(rsDados.getString(15));
                    arrBuscaColetaItMontado.add(rsDados.getString(16));
                    arrBuscaColetaItUrgente.add(rsDados.getString(18));
                    arrBuscaColetaItDesenho.add(rsDados.getString(17));
                    arrBuscaColetaItObs.add(rsDados.getString(22));
                    arrBuscaColetaItVlrTotal.add(rsDados.getString(9));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (contaDadosRs > 0) {
                        if (contaDadosRs > numMaxIt) {
                            if ((contaDadosRs % numMaxIt) == 0) {
                                numeroPg = contaDadosRs / numMaxIt;
                            } else {
                                numeroPg = (contaDadosRs / numMaxIt + 1);
                            }
                        }

                        for (i = 1; i <= numeroPg; i++) {
                            Paint dadosFilial = new Paint();
                            Paint imagens = new Paint();
                            Paint cabecalho = new Paint();
                            Paint corpoEsquerda = new Paint();
                            Paint corpoEsquerdaBold = new Paint();
                            Paint corpoDireita = new Paint();
                            Paint corpoDireitaBold = new Paint();
                            Paint valorTotal = new Paint();
                            Paint pVale = new Paint();
                            Paint tabelaItens = new Paint();
                            Paint termos = new Paint();
                            Paint tabelaItensDesenho = new Paint();
                            Paint recusada = new Paint();

                            tabelaItensDesenho.setTextSize(12);

                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigth, i).create();
                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                            Canvas canvas = page.getCanvas();

                            ResultSet rsCabec = conecta.buscaColetasExtCabec(dataConv, numeroColeta, nome, filial, codVendedor, false, "", "");
                            while (rsCabec.next()) {
                                vendedor = rsCabec.getString(28);

                                if (!rsCabec.getString(13).equals("")) {
                                    nomeBorr = rsCabec.getString(14);
                                    documento = rsCabec.getString(15).replaceAll("\\D", "");
                                    if (documento.length() == 14) {
                                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                                    } else {
                                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                                    }
                                    borracheiro = documento + " | " + nomeBorr;
                                } else {
                                    borracheiro = "SEM BORRACHEIRO";
                                }

                                horaColeta = rsCabec.getString(21);
                                infoAdicional = rsCabec.getString(12);
                                dataChegada = rsCabec.getString(31);
                                orcamento = (rsCabec.getString(32).equals("T") ? "COM" : "SEM") + " ORÇAMENTO.";
                                coletadoPor = rsCabec.getString(36);

                                /**
                                 * BUSCA NOVO CLIENTE E CABEÇALHO *
                                 * **/
                                if (conecta.clienteNovo("COL_CLI_RZ_SOCIAL", codCli, lojaCli) != null) {
                                    documento = conecta.clienteNovo("COL_CLI_DOC_CLI", codCli, lojaCli).replaceAll("\\D", "");
                                    email = conecta.clienteNovo("COL_CLI_EMIAL", codCli, lojaCli).trim();
                                    telefone = conecta.clienteNovo("COL_CLI_ENT_FONE", codCli, lojaCli);
                                    inscrEstadual = conecta.clienteNovo("COL_CLI_IE", codCli, lojaCli);
                                    endereco = conecta.clienteNovo("COL_CLI_ENT_END", codCli, lojaCli);
                                    enderecoCobr = conecta.clienteNovo("COL_CLI_COBR_END", codCli, lojaCli);
                                    estado = conecta.clienteNovo("COL_CLI_ENT_EST", codCli, lojaCli);
                                    municipio = conecta.clienteNovo("COL_CLI_ENT_MUNIC", codCli, lojaCli);
                                    cep = conecta.clienteNovo("COL_CLI_ENT_CEP", codCli, lojaCli);
                                } else {
//                                documento = conecta.cliente("A1_CGC", codCli, lojaCli).replaceAll("\\D", "");
//                                email = conecta.cliente("A1_EMAIL", codCli, lojaCli).trim();
//                                telefone = conecta.cliente("A1_TEL", codCli, lojaCli);
//                                inscrEstadual = conecta.cliente("A1_INSCR", codCli, lojaCli);
//                                endereco = conecta.cliente("A1_END", codCli, lojaCli);
//                                enderecoCobr = conecta.cliente("A1_ENDCOB", codCli, lojaCli);
//                                estado = conecta.cliente("A1_EST", codCli, lojaCli);
//                                municipio = conecta.cliente("A1_MUN", codCli, lojaCli);
//                                cep = conecta.cliente("A1_CEP", codCli, lojaCli);

                                    db.buscaClientesColeta("", "", "", new ArrayList<>(0), codCli, lojaCli);
                                    documento = db.cgcCliente;
                                    if (documento.length() == 14) {
                                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                                    } else {
                                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                                    }
                                    email = db.emailCli;
                                    telefone = db.foneCli;
                                    inscrEstadual = db.ieCli;
                                    endereco = db.endCli;
                                    estado = db.ufCli;
                                    municipio = db.munCli;
                                    cep = db.cepCli;
                                    enderecoCobr = db.endCobCli;
                                }

                                if (documento.length() == 14) {
                                    documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                                } else {
                                    documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                                }

                                ResultSet rsCondPgto = conecta.condPgto(rsCabec.getString(10));
                                formaPgto = rsCabec.getString(11);

                                while (rsCondPgto.next()) {
                                    condPgto = rsCondPgto.getString(3).trim();
                                }
                            }

                            conecta.selecionaDadosFilial(filial);
                            String nomeFil = conecta.descricaoFilial;
                            String cnpjFil = conecta.cnpjFilial;
                            String enderFil = conecta.enderecoFilial;
                            String cidadeFil = conecta.cidadeFilial;
                            String cepFil = conecta.cepFilial;
                            String foneFil = conecta.telefoneFilial;

                            if (filial.contains("0401")) {
                                idLogo = R.drawable.logovb;
                            } else if (filial.contains("0801")) {
                                idLogo = R.drawable.logovlk;
                            } else if (filial.contains("0901")) {
                                idLogo = R.drawable.logovp;
                            } else if (filial.contains("1001")) {
                                idLogo = R.drawable.logogv;
                            } else if (filial.contains("1101") || filial.contains("1201")) {
                                idLogo = R.drawable.logoost;
                            } else if (filial.contains("1301") || filial.contains("1401")) {
                                idLogo = R.drawable.logolauxvach;
                            } else {
                                idLogo = R.drawable.logovachileski;
                            }

                            Bitmap bmpLogo = BitmapFactory.decodeResource(res, idLogo);
                            Bitmap escalaLogo = Bitmap.createScaledBitmap(bmpLogo, 798, 262, false);

                            File DIRECTORY = context.getExternalFilesDir("Assinaturas");
                            ///Environment.getExternalStorageDirectory().getAbsolutePath()+"/VachileskiMobi/Assinaturas/"; //Environment.getDataDirectory().getAbsolutePath() + "/VachileskiMobi/Assinaturas/";
                            String pic_nameCli, pic_nameBorr;
                            String StoredPathCli, StoredPathBorr;

//                            if (fileRoot.exists()) {
//                                //noinspection ResultOfMethodCallIgnored
//                                fileRoot.delete();
//                            }
//
//                            if (!fileRoot.exists()) {
//                                //noinspection ResultOfMethodCallIgnored
//                                fileRoot.mkdirs();
//                            }

                            if (nomeFil != null) {
                                if (!foneFil.trim().equals("")) {
                                    if (foneFil.substring(1).equals("0")) {
                                        foneFil = foneFil.replace(foneFil.substring(1), "");
                                    }
                                }

                                if (cnpjFil.trim().equals("")) {
                                    if (cnpjFil.length() == 14) {
                                        cnpjFil = cnpjFil.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                                    } else {
                                        cnpjFil = cnpjFil.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                                    }
                                }

                                /** DADOS DA FILIAL DA COLETA **/
                                dadosFilial.setTextAlign(Paint.Align.LEFT);
                                dadosFilial.setTextSize(20);
                                dadosFilial.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                                canvas.drawText(nomeFil, 50, 60, dadosFilial); //RAZAO SOCIAL
                                canvas.drawText("CNPJ: " + cnpjFil, 50, 90, dadosFilial); //CNPJ
                                canvas.drawText("Ender.: " + enderFil, 50, 120, dadosFilial); //ENDEREÇO
                                canvas.drawText("CEP: " + cepFil.replaceAll("([0-9]{5})([0-9]{3})", "$1\\-$2") + " - " + cidadeFil, 50, 150, dadosFilial); //CEP CIDADE E ESTADO
                                canvas.drawText("Fone.: " + foneFil.replaceAll("([0-9]{2})([0-9]{4})", "($1\\)$2\\-"), 50, 180, dadosFilial); //TELEFONE

                                /** LOGO **/
                                canvas.drawBitmap(escalaLogo, (pageWidth / 2), 10, imagens); //INICIA NO CENTRO DA PG
                            } else {
                                canvas.drawBitmap(escalaLogo, ((pageWidth / 2) - (escalaLogo.getWidth() / 2)), 10, imagens); //CENTRO DA PG DE ACORDO COM A IMG
                            }

                            /** ABAIXO DO LOGO **/
                            cabecalho.setTextAlign(Paint.Align.LEFT);
                            cabecalho.setTextSize(20);
                            cabecalho.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                            canvas.drawText("Caro cliente,", 50, 250, cabecalho);
                            canvas.drawText("Segue abaixo, os dados da Coleta Eletrônica realizada pelo vendedor " + coletadoPor.trim().toUpperCase().replace(".", " ") + ".", 50, 310, cabecalho);

                            cabecalho.setColor(res.getColor(R.color.red));
                            canvas.drawText("Data da Coleta: " + GettersSetters.converteData(dataConv.substring(0, 4) + "-" + dataConv.substring(4, 6) + "-" + dataConv.substring(6, 8), "EN") + " às " + horaColeta, 50, 340, cabecalho);

                            if (!dataChegada.trim().equals("") && dataChegada != null) {
                                canvas.drawText("Data prevista da chegada dos Pneus na Filial: " + dataChegada, 50, 370, cabecalho);
                            }

                            cabecalho.setColor(res.getColor(R.color.colorAccent));
                            canvas.drawText("Número da Coleta Eletrônica: " + numeroColeta, 50, 405, cabecalho);

                            /** CÓDIGO DE BARRAS **/
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(filial + numeroColeta, BarcodeFormat.CODE_128, 260, 75);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmapBarcode = barcodeEncoder.createBitmap(bitMatrix);
                                //imageView.setImageBitmap(bitmap);
                                canvas.drawBitmap(bitmapBarcode, 1380, 300, imagens);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            /**
                             * IDENTIFICAÇÃO DAS PAGINAÇÃO
                             * **/
                            cabecalho.setColor(res.getColor(R.color.blue));
                            cabecalho.setTextSize(14);
                            canvas.drawText("Página " + i + "/" + numeroPg, 1585, 405, cabecalho);

                            corpoEsquerda.setTextAlign(Paint.Align.LEFT);
                            corpoEsquerda.setTextSize(20);
                            corpoEsquerda.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                            corpoEsquerdaBold.setTextAlign(Paint.Align.LEFT);
                            corpoEsquerdaBold.setTextSize(20);
                            corpoEsquerdaBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            corpoEsquerdaBold.setColor(res.getColor(R.color.blue));

                            canvas.drawLine(50, 425, pageWidth - 50, 425, corpoEsquerda);
                            if (!filial.contains("1101") && !filial.contains("1201")) {
                                canvas.drawLine(50, 425, 50, 825, corpoEsquerda);
                                canvas.drawLine((pageWidth / 2) - 120, 425, (pageWidth / 2) - 120, 825, corpoEsquerda); //LINHA MEIO TABELA
                            } else {
                                canvas.drawLine(50, 425, 50, 785, corpoEsquerda);
                                canvas.drawLine((pageWidth / 2) - 120, 425, (pageWidth / 2) - 120, 785, corpoEsquerda); //LINHA MEIO TABELA
                            }
                            canvas.drawText("Razão Social", 60, 450, corpoEsquerda);
                            canvas.drawLine(50, 465, pageWidth - 50, 465, corpoEsquerda);
                            canvas.drawText("CPF/CNPJ", 60, 490, corpoEsquerda);
                            canvas.drawLine(50, 505, pageWidth - 50, 505, corpoEsquerda);
                            canvas.drawText("Inscr. Estadual", 60, 530, corpoEsquerda);
                            canvas.drawLine(50, 545, pageWidth - 50, 545, corpoEsquerda);
                            canvas.drawText("Endereço", 60, 570, corpoEsquerda);
                            canvas.drawLine(50, 585, pageWidth - 50, 585, corpoEsquerda);
                            canvas.drawText("E-mail", 60, 610, corpoEsquerda);
                            canvas.drawLine(50, 625, pageWidth - 50, 625, corpoEsquerda);
                            canvas.drawText("Telefone", 60, 650, corpoEsquerda);
                            canvas.drawLine(50, 665, pageWidth - 50, 665, corpoEsquerda);
                            canvas.drawText("Endereço Cobrança", 60, 690, corpoEsquerda);
                            canvas.drawLine(50, 705, pageWidth - 50, 705, corpoEsquerda);
                            canvas.drawText("Condição de Pagamento", 60, 730, corpoEsquerda);
                            canvas.drawLine(50, 745, pageWidth - 50, 745, corpoEsquerda);
                            canvas.drawText("Informações Adicionais", 60, 770, corpoEsquerdaBold);
                            canvas.drawLine(50, 785, pageWidth - 50, 785, corpoEsquerda);
                            /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                            if (!filial.contains("1101") && !filial.contains("1201")) {
                                canvas.drawText("Borracheiro", 60, 810, corpoEsquerda);
                                canvas.drawLine(50, 825, pageWidth - 50, 825, corpoEsquerda);
                                canvas.drawLine(pageWidth - 50, 425, pageWidth - 50, 825, corpoEsquerda);
                            } else {
                                canvas.drawLine(pageWidth - 50, 425, pageWidth - 50, 785, corpoEsquerda);
                            }

                            corpoDireitaBold.setTextAlign(Paint.Align.LEFT);
                            corpoDireitaBold.setTextSize(20);
                            corpoDireitaBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            corpoDireitaBold.setColor(res.getColor(R.color.blue));

                            corpoDireita.setTextSize(20);
                            canvas.drawText(nome, (pageWidth / 2) - 50, 450, corpoDireita);
                            canvas.drawText(documento, (pageWidth / 2) - 50, 490, corpoDireita);
                            canvas.drawText(inscrEstadual, (pageWidth / 2) - 50, 530, corpoDireita);
                            canvas.drawText(endereco.trim() + " | " + municipio.trim() + "-" + estado.trim() + " " + cep.trim(), (pageWidth / 2) - 50, 570, corpoDireita);
                            canvas.drawText(email.trim().equals(".") ? "SEM E-MAIL" : email.trim(), (pageWidth / 2) - 50, 610, corpoDireita);
                            canvas.drawText(telefone, (pageWidth / 2) - 50, 650, corpoDireita);
                            canvas.drawText(enderecoCobr, (pageWidth / 2) - 50, 690, corpoDireita);
                            canvas.drawText(condPgto + " | " + formaPgto, (pageWidth / 2) - 50, 730, corpoDireita);
                            if (infoAdicional.trim().length() > 70) {
                                corpoDireitaBold.setTextSize(16);
                                canvas.drawText(infoAdicional.substring(0, 69), (pageWidth / 2) - 50, 763, corpoDireitaBold);
                                canvas.drawText(infoAdicional.substring(69) + " | -> " + orcamento, (pageWidth / 2) - 50, 780, corpoDireitaBold);
                            } else {
                                canvas.drawText(infoAdicional + " | -> " + orcamento, (pageWidth / 2) - 50, 770, corpoDireitaBold);
                            }
                            /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                            if (!filial.contains("1101") && !filial.contains("1201")) {
                                canvas.drawText(borracheiro, (pageWidth / 2) - 50, 810, corpoDireita);
                            }

                            /** VALOR TOTAL **/
                            valorTotal.setTextAlign(Paint.Align.LEFT);
                            valorTotal.setTextSize(30);
                            valorTotal.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            valorTotal.setColor(res.getColor(R.color.red));
                            canvas.drawText("Valor Total* R$: " + valor, 60, 900, valorTotal);

                            /* VALE */
                            if (vale.length() > 0) {
                                pVale.setTextAlign(Paint.Align.LEFT);
                                pVale.setTextSize(30);
                                pVale.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                pVale.setColor(res.getColor(R.color.blue));
                                canvas.drawText("Nota/Série: " + vale, (pageWidth/2) + 300, 900, pVale);
                            }

                            /** CABEÇALHO DA TABELA **/
                            canvas.drawLine(50, 950, pageWidth - 50, 950, corpoEsquerda); //LINHA SUPERIOR
                            canvas.drawLine(50, 950, 50, 1000, corpoEsquerda); //LINHA ESQUERDA
                            canvas.drawText("Item", 53, 975, corpoEsquerda);
                            canvas.drawLine(110, 950, 110, 1000, corpoEsquerda); //LINHA ITEM X QTDE
                            canvas.drawText("Qtde", 113, 975, corpoEsquerda);
                            canvas.drawLine(170, 950, 170, 1000, corpoEsquerda); //LINHA QTDE X BITOLA
                            canvas.drawText("Bitola", 354, 975, corpoEsquerda);
                            canvas.drawLine(520, 950, 520, 1000, corpoEsquerda); //LINHA BITOLA X MARCA
                            canvas.drawText("Marca", 535, 975, corpoEsquerda);
                            canvas.drawLine(637, 950, 637, 1000, corpoEsquerda); //LINHA MARCA X MODELO
                            canvas.drawText("Modelo", 640, 975, corpoEsquerda);
                            canvas.drawLine(754, 950, 754, 1000, corpoEsquerda); //LINHA MODELO X SERIE
                            canvas.drawText("Série", 770, 975, corpoEsquerda);
                            canvas.drawLine(871, 950, 871, 1000, corpoEsquerda); //LINHA SERIE X MONTADO
                            canvas.drawText("DOT", 885, 975, corpoEsquerda);
                            canvas.drawLine(988, 950, 988, 1000, corpoEsquerda); //LINHA MONTADO X DOT
                            canvas.drawText("Montado?", 991, 975, corpoEsquerda);
                            canvas.drawLine(1105, 950, 1105, 1000, corpoEsquerda); //LINHA DOT X URGENTE
                            canvas.drawText("Urgente", 1108, 975, corpoEsquerda);
                            canvas.drawLine(1222, 950, 1222, 1000, corpoEsquerda); //LINHA URGENTE X DESENHO
                            canvas.drawText("Desenho", 1225, 975, corpoEsquerda);
                            canvas.drawLine(1339, 950, 1339, 1000, corpoEsquerda); //LINHA DESENHO X OBSERVACAO
                            canvas.drawText("Observação", 1355, 975, corpoEsquerda);
                            canvas.drawLine(1590, 950, 1590, 1000, corpoEsquerda); //LINHA OBSERVACAO X VALOR
                            canvas.drawText("Valor Total", 1592, 975, corpoEsquerda);
                            canvas.drawLine(pageWidth - 50, 950, pageWidth - 50, 1000, corpoEsquerda); //LINHA DIREITA
                            canvas.drawLine(50, 1000, pageWidth - 50, 1000, corpoEsquerda); //LINHA INFERIOR

                            /**
                             * BUSCA DOS ITENS
                             * **/
                            int linhaIni = 1025; // somar 25 nas próximas linhas

                            tabelaItens.setTextSize(15);
                            for (int j = proxIt; j < arrBuscaColetaItFilial.size(); j++) {
                                canvas.drawText(arrBuscaColetaItItem.get(j), 70, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItQtd.get(j), 125, linhaIni, tabelaItens);
                                if (arrBuscaColetaItBitola.get(j).trim().length() > 42) {
                                    canvas.drawText(arrBuscaColetaItBitola.get(j).substring(0, 41), 173, linhaIni, tabelaItens);
                                    linhaIni += 15;
                                    canvas.drawText(arrBuscaColetaItBitola.get(j).substring(41), 173, linhaIni, tabelaItens);
                                } else {
                                    canvas.drawText(arrBuscaColetaItBitola.get(j), 173, linhaIni, tabelaItens);
                                }
                                //canvas.drawText(arrBuscaColetaItBitola.get(j), 173, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItMarca.get(j), 527, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItModelo.get(j), 640, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItSerie.get(j), 757, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItDot.get(j), 874, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItMontado.get(j).equals("0") ? "Montado" : "", 991, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItUrgente.get(j).equals("1") ? "Urgente" : "", 1108, linhaIni, tabelaItens);
                                canvas.drawText(arrBuscaColetaItDesenho.get(j), 1225, linhaIni, tabelaItensDesenho);
                                if (arrBuscaColetaItObs.get(j).trim().length() > 29) {
                                    String infoAdic = String.format("%-100.100s", arrBuscaColetaItObs.get(j).trim()); //ADICIONA 100 POSICOES PARA NÃO DAR EXCEÇÃO

                                    Paint tabelaItensQuebra = new Paint();
                                    tabelaItensQuebra.setTextSize(13);
                                    canvas.drawText(infoAdic.substring(0, 29).trim(), 1344, linhaIni, tabelaItensQuebra);
                                    linhaIni += 15;
                                    canvas.drawText(infoAdic.substring(29, 57).trim(), 1344, linhaIni, tabelaItensQuebra);
                                    linhaIni += 15;
                                    canvas.drawText(infoAdic.substring(57, 86).trim(), 1344, linhaIni, tabelaItensQuebra);
                                    linhaIni += 15;
                                    canvas.drawText(infoAdic.substring(86).trim(), 1344, linhaIni, tabelaItensQuebra);
                                } else {
                                    canvas.drawText(arrBuscaColetaItObs.get(j), 1344, linhaIni, tabelaItens);
                                }
                                canvas.drawText(df.format(Double.parseDouble(arrBuscaColetaItVlrTotal.get(j))), 1596, linhaIni, tabelaItens);
                                canvas.drawLine(50, linhaIni + 10, pageWidth - 50, linhaIni + 10, corpoEsquerda); //LINHA INFERIOR
                                linhaIni += 30;

                                contItPPg++;
                                if (contItPPg == numMaxIt) {
                                    proxIt = j + 1;
                                    contItPPg = 0;
                                    break;
                                }
                            }

                            /** DIVISÓRIAS **/
                            canvas.drawLine(50, 1000, 50, linhaIni - 20, corpoEsquerda); //LINHA ESQUERDA
                            canvas.drawLine(110, 1000, 110, linhaIni - 20, corpoEsquerda); //LINHA ITEM X QTDE
                            canvas.drawLine(170, 1000, 170, linhaIni - 20, corpoEsquerda); //LINHA QTDE X BITOLA
                            canvas.drawLine(520, 1000, 520, linhaIni - 20, corpoEsquerda); //LINHA BITOLA X MARCA
                            canvas.drawLine(637, 1000, 637, linhaIni - 20, corpoEsquerda); //LINHA MARCA X MODELO
                            canvas.drawLine(754, 1000, 754, linhaIni - 20, corpoEsquerda); //LINHA MODELO X SERIE
                            canvas.drawLine(871, 1000, 871, linhaIni - 20, corpoEsquerda); //LINHA SERIE X MONTADO
                            canvas.drawLine(988, 1000, 988, linhaIni - 20, corpoEsquerda); //LINHA MONTADO X DOT
                            canvas.drawLine(1105, 1000, 1105, linhaIni - 20, corpoEsquerda); //LINHA DOT X URGENTE
                            canvas.drawLine(1222, 1000, 1222, linhaIni - 20, corpoEsquerda); //LINHA URGENTE X DESENHO
                            canvas.drawLine(1339, 1000, 1339, linhaIni - 20, corpoEsquerda); //LINHA DESENHO X OBSERVACAO
                            canvas.drawLine(1590, 1000, 1590, linhaIni - 20, corpoEsquerda); //LINHA OBSERVACAO X VALOR
                            canvas.drawLine(pageWidth - 50, 1000, pageWidth - 50, linhaIni - 20, corpoEsquerda); //LINHA DIREITA

                            /** ASSINATURAS **/
                            /** CLIENTE **/

                            byte[] blobCli = conecta.buscaAssinaturas("COL_CABEC_ASSIN_CLI", codCli, lojaCli, numeroColeta, dataConv);
                            pic_nameCli = "ASSINATURA-CLIENTE.png";
                            StoredPathCli = DIRECTORY + "/" + pic_nameCli;
                            /** DELETA ARQUIVOS PNG */
                            File fileAssinaturaCli = new File(DIRECTORY, pic_nameCli);
                            //noinspection ResultOfMethodCallIgnored
                            fileAssinaturaCli.delete();
                            /****/

                            /** CONVERTE ASSINATURAS DE BYTE PARA PNG **/
                            byte[] dataBmpAssinCli = Base64.decode(blobCli, Base64.DEFAULT);
                            Bitmap bitmapAssinaturaCli = BitmapFactory.decodeByteArray(dataBmpAssinCli, 0, dataBmpAssinCli.length);
                            if (bitmapAssinaturaCli != null) {
                                bitmapAssinaturaCli = Bitmap.createScaledBitmap(bitmapAssinaturaCli, 794, 420, true);
                                ImageView image = new ImageView(context);
                                image.setImageBitmap(bitmapAssinaturaCli);

                                try {
                                    FileOutputStream mFileOutStream = new FileOutputStream(StoredPathCli);
                                    bitmapAssinaturaCli.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                                    mFileOutStream.flush();
                                    mFileOutStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (borracheiro.equals("SEM BORRACHEIRO")) {
                                    canvas.drawBitmap(bitmapAssinaturaCli, (pageWidth / 2) - (bitmapAssinaturaCli.getWidth() / 2), linhaIni, imagens);
                                    //canvas.drawText(nome, (bitmapAssinaturaCli.getWidth() / 2) - 40, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                                    canvas.drawText("ASSINATURA CLIENTE", (pageWidth / 2) - 100, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                                } else {
                                    canvas.drawBitmap(bitmapAssinaturaCli, 50, linhaIni, imagens);
                                    canvas.drawText("ASSINATURA CLIENTE", (bitmapAssinaturaCli.getWidth() / 2) - 40, linhaIni + bitmapAssinaturaCli.getHeight() + 20, corpoEsquerda);
                                }
                            }

                            /* PARA OST NÃO SAIRÁ IMPRESSO NO PDF AS INFORMAÇÕES DE BORRACHEIROS */
                            if (!filial.contains("1101") && !filial.contains("1201")) {
                                /** BORRACHEIRO **/
                                if (!borracheiro.equals("SEM BORRACHEIRO")) {
                                    byte[] blobBorr = conecta.buscaAssinaturas("COL_CABEC_ASSIN_BORR", codCli, lojaCli, numeroColeta, dataConv);

                                    if (blobBorr != null) {
                                        pic_nameBorr = "ASSINATURA-BORRACH.png";
                                        StoredPathBorr = DIRECTORY + "/" + pic_nameBorr;
                                        /** DELETA ARQUIVOS PNG */
                                        File fileAssinaturaBorr = new File(DIRECTORY, pic_nameBorr);
                                        //noinspection ResultOfMethodCallIgnored
                                        fileAssinaturaBorr.delete();
                                        /****/

                                        /** CONVERTE ASSINATURAS DE BYTE PARA PNG **/
                                        byte[] dataBmpAssinBorr = Base64.decode(blobBorr, Base64.DEFAULT);
                                        Bitmap bitmapAssinaturaBorr = BitmapFactory.decodeByteArray(dataBmpAssinBorr, 0, dataBmpAssinBorr.length);

                                        if (bitmapAssinaturaBorr != null) {
                                            bitmapAssinaturaBorr = Bitmap.createScaledBitmap(bitmapAssinaturaBorr, 794, 420, true);

                                            ImageView imageBorr = new ImageView(context);
                                            imageBorr.setImageBitmap(bitmapAssinaturaBorr);

                                            try {
                                                FileOutputStream mFileOutStream = new FileOutputStream(StoredPathBorr);
                                                bitmapAssinaturaBorr.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                                                mFileOutStream.flush();
                                                mFileOutStream.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            canvas.drawBitmap(bitmapAssinaturaBorr, bitmapAssinaturaCli.getWidth() + 110, linhaIni, imagens);
                                            //canvas.drawText(nomeBorr, (bitmapAssinaturaCli.getWidth() + bitmapAssinaturaBorr.getWidth() / 2) + 20, linhaIni + bitmapAssinaturaBorr.getHeight() + 20, corpoEsquerda);
                                            canvas.drawText("ASSINATURA BORRACHEIRO", (bitmapAssinaturaCli.getWidth() + bitmapAssinaturaBorr.getWidth() / 2) + 20, linhaIni + bitmapAssinaturaBorr.getHeight() + 20, corpoEsquerda);
                                        }
                                    }
                                }
                            }

                            if (bitmapAssinaturaCli != null) {
                                linha = linhaIni + bitmapAssinaturaCli.getHeight() + 100;
                            } else {
                                linha = linhaIni + 520;
                            }

                            canvas.drawLine(50, linha - 30, pageWidth - 50, linha - 30, corpoEsquerda);
                            corpoEsquerda.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("TERMOS", pageWidth / 2, linha, corpoEsquerda);
                            termos.setTextSize(20);
                            canvas.drawText(res.getString(R.string.termo1).substring(1, 154), 50, linha + 50, termos);
                            canvas.drawText(res.getString(R.string.termo1).substring(155, 309), 50, linha + 75, termos);
                            canvas.drawText(res.getString(R.string.termo1).substring(310), 50, linha + 100, termos);

                            canvas.drawText(res.getString(R.string.termo2).substring(1, 141).replace("[NOME_EMPRESA]", nomeFil.trim().toUpperCase()), 50, linha + 135, termos);
                            canvas.drawText(res.getString(R.string.termo2).substring(142).replace("[NOME_EMPRESA]", nomeFil.trim().toUpperCase()), 50, linha + 160, termos);

                            termos.setColor(res.getColor(R.color.red));
                            canvas.drawText("* " + res.getString(R.string.termo3), 50, linha + 195, termos);

                            corpoEsquerda.setColor(res.getColor(R.color.blue));
                            corpoEsquerda.setTextSize(18);
                            corpoEsquerda.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            corpoEsquerda.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("Este arquivo foi gerado automaticamente pelo sistema de Coleta Eletrônica do Grupo Vachileski", pageWidth / 2, pageHeigth - 50, corpoEsquerda);

                            /** QUANDO RECUSADA **/
                            if (status.equals("6")) { //STATUS IGUAL AO DO cRecap
                                canvas.save();
                                recusada.setColor(res.getColor(R.color.red_light2));
                                recusada.setTextSize(200);
                                canvas.rotate(-45, canvas.getWidth() / 2, // px, center x
                                        canvas.getHeight() / 2); // py, center y);
                                canvas.drawText("CRÉDITO RECUSADO", (pageWidth / 2) - 950, 1350, recusada);
                                canvas.restore();
                            }

                            pdfDocument.finishPage(page);
                            /** -- FIM DA PÁGINA -- **/
                        }

                        FuncoesGenericas.deletaArquivos(ROOT_PATH);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return geraArqPdf(pdfDocument, ROOT_PATH, FILE_NAME);
    }

    public File geraArqPdf(PdfDocument pdfDocument, File ROOT_PATH, String FILE_NAME) {
        File file = new File(ROOT_PATH + "/" + FILE_NAME); //CRIA ARQUIVO PDF

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }

        pdfDocument.close();

        return file;
    }

//    public void deletaPDF(File caminho) {
//        String files[] = caminho.list();
//        File filePdf;
//        if (files != null && files.length > 0) {
//            for (String nomePdf : files) {
//                filePdf = new File(caminho + "/" + nomePdf);
//                if (filePdf.exists()) {
//                    filePdf.delete();
//                }
//            }
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public byte[] pdfToImage(Context context, File pdf, int largura, int altura) {
////        Bitmap bitmap = null;
////        ByteArrayOutputStream bmpImgPdf = new ByteArrayOutputStream();
//
////        try {
////            PdfRenderer render = new PdfRenderer(ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY));
////            final int pageCount = render.getPageCount();
////            if (pageCount == 1) {
////                for (int i = 0; i < pageCount; i++) {
////                    PdfRenderer.Page page = render.openPage(i);
////                    bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888);
////                    Canvas canvas = new Canvas(bitmap);
////                    canvas.drawColor(Color.WHITE);
////                    canvas.drawBitmap(bitmap, 0, 0, null);
////                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
////                    page.close();
////                }
////                render.close();
////
////                //SALVA PDF EM IMAGEM E GERA ARQUIVO
////                File ROOT_PATH = context.getExternalFilesDir("Arquivos-Whatsapp"); //Environment.getExternalStorageDirectory().getPath() + "/VachileskiMobi/";
////                File fileRoot = new File(ROOT_PATH + "/" + "img.png");
////
////                //GettersSetters.setImagePath(ROOT_PATH + "/" + "img.png");
////
////                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bmpImgPdf);
////                FileOutputStream saidaFotoComprimida = new FileOutputStream(fileRoot);
////                saidaFotoComprimida.write(bmpImgPdf.toByteArray());
////                saidaFotoComprimida.close();
////
////                return bmpImgPdf.toByteArray();
////            } else {
//        /** RENDERIZA A FILE PDF EM BYTES **/
////                byte[] pdfByte = null;
////                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                    pdfByte = Files.readAllBytes(Paths.get(pdf.getPath()));
////                }
//        Path pdfPath = Paths.get(String.valueOf(pdf));
//        try {
//            return Files.readAllBytes(pdfPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
////            //below is the different part
////            File someFile = new File("java2.pdf");
////            FileOutputStream fos = new FileOutputStream(someFile);
////            fos.write(bytes);
////            fos.flush();
////            fos.close();
//    }
}