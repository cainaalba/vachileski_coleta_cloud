package pneus.vachileski_mobi_funcoes_genericas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pneus.vachileski_mobi_apanhe_pneus.ColetaBusca;
import pneus.vachileski_mobi_apanhe_pneus.R;

@SuppressWarnings("DanglingJavadoc")
public class ImprimeColeta extends AppCompatActivity {
    EditText cmp;
    TextView txtStatus;
    Button imprimir, conectar;
    public Printing printing = null;
    public PrintingCallback printingCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprime_coleta);

        txtStatus = findViewById(R.id.txtStatusConexao);
        conectar = findViewById(R.id.btnConectarImpressora);
        imprimir = findViewById(R.id.btnImprimir);
        cmp = findViewById(R.id.cmp);

        imprimir.setEnabled(false);
        conectar.setEnabled(false);

        inicializa(this);
    }

    public void inicializa(Context context) {
        Printooth.INSTANCE.init(context);

        if (Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
            initViews(context);
            initListeners(context);
        }
    }

    private void initViews(Context context) {
//        if (Printooth.INSTANCE.getPairedPrinter() != null) {
//            Toast.makeText(context, Printooth.INSTANCE.hasPairedPrinter() ? Printooth.INSTANCE.getPairedPrinter().getName() : "Parear com impressora", Toast.LENGTH_SHORT).show();
//        }
        Printooth.INSTANCE.getPairedPrinter();
    }

    ///STATUS DA IMPRESSORA
    private void initListeners(Context context) {
        if (printing != null && printingCallback == null) {
            printingCallback = new PrintingCallback() {

                public void connectingWithPrinter() {
                    Toast.makeText(context,  Printooth.INSTANCE.hasPairedPrinter() ? "Imprimindo em " + Printooth.INSTANCE.getPairedPrinter().getName() : "Parear com impressora", Toast.LENGTH_SHORT).show();
                }

                public void printingOrderSentSuccessfully() {
                    Toast.makeText(context, "Impressão concluída!", Toast.LENGTH_SHORT).show();
                }

                public void connectionFailed(@NotNull String error) {
                    Toast.makeText(context, "Falha Conexão: " + error, Toast.LENGTH_SHORT).show();
                }

                public void onError(@NotNull String error) {
                    Toast.makeText(context, "onError: " + error, Toast.LENGTH_SHORT).show();
                }

                public void onMessage(@NotNull String message) {
                    Toast.makeText(context, "onMessage: " + message, Toast.LENGTH_SHORT).show();
                }
            };
            Printooth.INSTANCE.printer().setPrintingCallback(printingCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initListeners(this);
        } else {
            Toast.makeText(this, "Impressora não Conectada!", Toast.LENGTH_LONG).show();
        }
    }

    public void impressaoDeColetas(String numeroColeta, String data, String filial, String dodCli, String nomeCli, String enderecoCli, String foneCli, String total, int qtdeItens,
                                   String vendedor, Context context, ArrayList<String> arrDescrBitola, ArrayList<String> arrValorItem,
                                   String pagamento, ArrayList<String> arrMontado, ArrayList<String> arrSerie, ArrayList<String> arrDot, ArrayList<String> arrMarca) {

        inicializa(context);

        int spacoLinha = 10;
        int spacoLinha1 = 14;
        int spacoLinha2 = 7;

        ArrayList<Printable> printables = new ArrayList<>();

        printables.add((new TextPrintable.Builder())
                .setText("Comprovante de Coleta Eletronica\n")
                .setFontSize((byte) 1)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Numero: " + numeroColeta + "\n")
                .setLineSpacing((byte) spacoLinha1)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Data: " + data + "\n")
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Empresa: " + filial.trim() + "\n")
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Vendedor: " + vendedor.trim())
                .setLineSpacing((byte) spacoLinha)
                .setNewLinesAfter(1)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Dados do Cliente\n")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Nome: " + nomeCli.trim() + "\n")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC860())
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("CNPJ/CPF: " + dodCli + "\n")
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Endereco: " + enderecoCli + "\n")
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Telefone: " + foneCli.replaceAll("([0-9]{2})([0-9]{4})", "($1\\)$2\\-") + "\n") //TELEFONE
                .build());

//                    Resources resources = getResources();
//                    Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.teste);
//                    printables.add(new ImagePrintable.Builder(bitmap)
//                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
//                            .setNewLinesAfter(2)
//                            .build());

        printables.add((new TextPrintable.Builder())
                .setText("Itens\n")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setLineSpacing((byte) spacoLinha)
                .build());

        for (int i = 0; i < qtdeItens; i++) {
            printables.add((new TextPrintable.Builder())
                    .setText("Item " + (i + 1) + ":")
                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                    .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                    .build());

            printables.add((new TextPrintable.Builder())
                    .setText(" " + arrDescrBitola.get(i) + " - " + arrMarca.get(i) + "\n")
                    .setLineSpacing((byte) spacoLinha)
                    .build());

            printables.add((new TextPrintable.Builder())
                    .setText("S: " + arrSerie.get(i) + " D: " + arrDot.get(i) + " ")
                    .setLineSpacing((byte) spacoLinha)
                    .build());

            printables.add((new TextPrintable.Builder())
                    .setText((arrMontado.get(i).trim().equals("0") ? "Montado" : ""))
                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                    .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                    .setLineSpacing((byte) spacoLinha)
                    .build());

            printables.add((new TextPrintable.Builder())
                    .setText(" R$" + arrValorItem.get(i) + "\n")
                    .setLineSpacing((byte) spacoLinha)
                    .build());
        }

        printables.add((new TextPrintable.Builder())
                .setText("\n")
                .setLineSpacing((byte) spacoLinha2)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Pagamento: " + pagamento + "\n")
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Total de Itens: " + qtdeItens + "\n")
                .setFontSize((byte) 1)
                .setLineSpacing((byte) spacoLinha)
                .build());

        printables.add((new TextPrintable.Builder())
                .setText("Total: R$ " + total)
                .setFontSize((byte) 1)
                .setLineSpacing((byte) spacoLinha)
                .setNewLinesAfter(1)
                .build());

        /** CÓDIGO DE BARRAS **/
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(numeroColeta, BarcodeFormat.CODE_128, 260, 95);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmapBarcode = barcodeEncoder.createBitmap(bitMatrix);
            printables.add(new ImagePrintable.Builder(bitmapBarcode)
                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                    .build());

            printables.add((new TextPrintable.Builder())
                    .setText("\n[" + numeroColeta + "]")
                    .setLineSpacing((byte) spacoLinha)
                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                    .setNewLinesAfter(3)
                    .build());
        } catch (WriterException e) {
            e.printStackTrace();
            Log.v("Coleta","Cod Barra Impressao", e.fillInStackTrace());
        }

//        printables.add((new TextPrintable.Builder())
//                .setText("Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
//                        "Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
//                        "Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
//                        "Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
//                        "Lorem Ipsum Lorem Ipsum Lorem Ipsum ")
//                .setLineSpacing((byte) spacoLinha)
//                .setNewLinesAfter(3)
//                .build());

        printing.print(printables);
    }
}