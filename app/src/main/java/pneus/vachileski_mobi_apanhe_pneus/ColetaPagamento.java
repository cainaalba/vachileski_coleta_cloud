package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings("Convert2Lambda")
public class ColetaPagamento extends AppCompatActivity {
    Button avancar, voltar, btnAtualCondPgto;
    Spinner spnCondPgto;
    RadioButton rdbPgtoBoleto, rdbPgtoCarteira, rdbPgtoCartCred, rdbPgtoDepBanc;
    RadioGroup radioGruopFormasPgto;
    View viewSnackBar;
    LinearLayout llVencimentos;

    String descrFormaPgto = "", formaPgto = "", condicaoPgto = "", codCondPgto = "";

    ArrayList<String> arrcodCondPgto = new ArrayList<>();
    ArrayList<String> arrdescCondPgto = new ArrayList<>();
    ArrayList<String> arrformaCondPgto = new ArrayList<>();
    ArrayList<String> arrdescFormaCondPgto = new ArrayList<>();
    ArrayList<String> arrCondPgto = new ArrayList<>();

    public static ProgressDialog mProgressDialog;

    ConexaoBDInt db;
    VerificaCondPgto verificaCondPgto = null;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //BLOQUEIA PRINT
        setContentView(R.layout.activity_coleta_pagamento);
        setTitle(getString(R.string.tituloColeta) + " - Faturas");

        db = new ConexaoBDInt(this);

        avancar = findViewById(R.id.btnAvancar);
        voltar = findViewById(R.id.btnVoltar);
        radioGruopFormasPgto = findViewById(R.id.radioGruopFormasPgto);
        spnCondPgto = findViewById(R.id.spnCondPgto);
        rdbPgtoDepBanc = findViewById(R.id.rdbPgtoDepBanc);
        rdbPgtoBoleto = findViewById(R.id.rdbPgtoBoleto);
        rdbPgtoCarteira = findViewById(R.id.rdbPgtoCarteira);
        rdbPgtoCartCred = findViewById(R.id.rdbPgtoCartCred);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        llVencimentos = findViewById(R.id.llVencimentos);
        btnAtualCondPgto = findViewById(R.id.btnAtualCondPgto);

        radioGruopFormasPgto.clearCheck();
        avancar.setVisibility(View.GONE);
        avancar.setEnabled(true);
        llVencimentos.setVisibility(View.GONE);

        if (!CheckConnection.isConnected(this)) {
            btnAtualCondPgto.setVisibility(View.GONE);
        }

        btnAtualCondPgto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaCondPgto = new VerificaCondPgto();
                verificaCondPgto.execute();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avancar.setEnabled(false);

                if (radioGruopFormasPgto.getCheckedRadioButtonId() == 0) {
                    Snackbar.make(viewSnackBar, "Para prosseguir, selecione a Forma de Pagamento", Snackbar.LENGTH_LONG).show();
                } else if (GettersSetters.getCondPgto().trim().equals("") || condicaoPgto.trim().equals("")) {
                    Snackbar.make(viewSnackBar, "Para prosseguir, selecione a Condição de Pagamento", Snackbar.LENGTH_LONG).show();
                } else if (GettersSetters.getCodCondPgto().trim().equals("") || codCondPgto.trim().equals("")) {
                    Snackbar.make(viewSnackBar, "Cod da Condição de Pagamento inválido.\nTente novamente!", Snackbar.LENGTH_LONG).show();
                } else if (GettersSetters.getDescrFormaPgto().trim().equals("") || descrFormaPgto.trim().equals("")) {
                    Snackbar.make(viewSnackBar, "Descrição de Pagamento inválida.\nTente novamente!", Snackbar.LENGTH_LONG).show();
                } else {
                    Intent it = new Intent(ColetaPagamento.this, ColetaConclusao.class);
                    startActivity(it);
                }

                avancar.setEnabled(true);
            }
        });

        radioGruopFormasPgto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
            @Override
            public void onCheckedChanged(RadioGroup group, int radioGruopFormasPgto) {
                formaPgto = "";
                spnCondPgto.setAdapter(null);
                condicaoPgto = "";
                codCondPgto = "";
                descrFormaPgto = "";

                RadioButton rbFormaPgto = group.findViewById(radioGruopFormasPgto);
                if (rbFormaPgto != null && radioGruopFormasPgto > -1) {
                    if (rdbPgtoDepBanc.isChecked()) {
                        formaPgto = "DB";
                    } else if (rdbPgtoBoleto.isChecked()) {
                        formaPgto = "BOL";
                    } else if (rdbPgtoCarteira.isChecked()) {
                        formaPgto = "R$";
                    } else if (rdbPgtoCartCred.isChecked()) {
                        formaPgto = "CC";
                    }

                    llVencimentos.setVisibility(View.VISIBLE);
                    spnCondPgto.setVisibility(View.VISIBLE);

                    if (Double.parseDouble(GettersSetters.getValorColeta()) < 200 && formaPgto.trim().equals("BOL")
                            && (!GettersSetters.getFilial().startsWith("1101") && !GettersSetters.getFilial().startsWith("1201"))) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ColetaPagamento.this);
                        alertDialog.setIcon(getResources().getDrawable(R.drawable.money, null));
                        alertDialog.setTitle("Condições de Pagamento");
                        alertDialog.setMessage("Para vendas abaixo de R$200,00 não são permitidos parcelamentos em Boleto Bancário!");
                        alertDialog.setPositiveButton("OK", null);
                        Dialog dialog1 = alertDialog.create();
                        dialog1.show();

                        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Toast.makeText(ColetaPagamento.this, "Selecione a Condição de Pagamento", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(ColetaPagamento.this, "Selecione a Condição de Pagamento", Toast.LENGTH_LONG).show();
                    }
                } else {
                    llVencimentos.setVisibility(View.VISIBLE);
                    avancar.setVisibility(View.GONE);
                }
            }
        });

        spnCondPgto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                spnCondPgto.setAdapter(null);
                spnCondPgto.setAdapter(getCondPagtoApdt(ColetaPagamento.this, formaPgto));
                if (spnCondPgto.getAdapter() != null) {
                    spnCondPgto.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ColetaPagamento.this, "Realize a atualização das Condições de Pagamento!", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        spnCondPgto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrCondPgto.size() > 0) {
                    condicaoPgto = arrCondPgto.get((int) id);
                    codCondPgto = arrcodCondPgto.get((int) id);
                    descrFormaPgto = arrdescFormaCondPgto.get((int) id);

                    String[] condPgto = condicaoPgto.split(",");

                    if (condPgto.length > 1 && ((Double.parseDouble(GettersSetters.getValorColeta()) / condPgto.length) < 200) && formaPgto.trim().equals("BOL")
                            && (!GettersSetters.getFilial().startsWith("1101") && !GettersSetters.getFilial().startsWith("1201"))) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ColetaPagamento.this);
                        alertDialog.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.money, null));
                        alertDialog.setTitle("PARCELAMENTOS em BOLETO BANCÁRIO");
                        alertDialog.setMessage("O VALOR MÍNIMO por parcela permitido é de R$200,00. Não é possível PARCELAR essa venda em BOLETO BANCÁRIO!\nPara parcelamentos selecione outra FORMA DE PAGAMENTO.");
                        alertDialog.setPositiveButton("OK", null);
                        Dialog dialog1 = alertDialog.create();
                        dialog1.show();

                        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                spnCondPgto.setAdapter(null);
                                avancar.setVisibility(View.GONE);
                                GettersSetters.setCondPgto("");
                                GettersSetters.setCodCondPgto("");
                                GettersSetters.setDescrFormaPgto("");
                            }
                        });
                    } else {
                        avancar.setVisibility(View.VISIBLE);
                        GettersSetters.setCondPgto(condicaoPgto);
                        GettersSetters.setCodCondPgto(codCondPgto);
                        GettersSetters.setDescrFormaPgto(descrFormaPgto);
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ColetaPagamento.this);
                    alertDialog.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.money, null));
                    alertDialog.setTitle("SEM COND. DE PAGAMENTOS");
                    alertDialog.setMessage("Nenhuma CONDIÇÃO DE PAGAMENTO localizada, favor atualizar e tentar novamente.");
                    alertDialog.setPositiveButton("OK", null);
                    Dialog dialog1 = alertDialog.create();
                    dialog1.show();

                    dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            spnCondPgto.setAdapter(null);
                            avancar.setVisibility(View.GONE);
                            GettersSetters.setCondPgto("");
                            GettersSetters.setCodCondPgto("");
                            GettersSetters.setDescrFormaPgto("");
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private ArrayAdapter<String> getCondPagtoApdt(Context context, String formaPgto) {
        arrcodCondPgto.clear();
        arrdescCondPgto.clear();
        arrformaCondPgto.clear();
        arrdescFormaCondPgto.clear();
        arrCondPgto.clear();

        db.buscaCondPgto(formaPgto, "");

        arrcodCondPgto.addAll(db.arrcodCondPgto);
        arrdescCondPgto.addAll(db.arrdescCondPgto);
        arrformaCondPgto.addAll(db.arrformaCondPgto);
        arrdescFormaCondPgto.addAll(db.arrdescFormaCondPgto);
        arrCondPgto.addAll(db.arrCondPgto);

        if (arrcodCondPgto.size() > 0) {
            String[] arrDescCondicoes = new String[arrdescCondPgto.size()];
            String[] arrCodCond = new String[arrdescCondPgto.size()];
            for (int i = 0; i < arrdescCondPgto.size(); i++) {
                arrDescCondicoes[i] = arrdescCondPgto.get(i);
                arrCodCond[i] = arrcodCondPgto.get(i);
            }
            avancar.setVisibility(View.VISIBLE);
            avancar.setEnabled(true);

            return new ArrayAdapter<>(context, R.layout.text_view_item_high, arrDescCondicoes);
        } else {
            avancar.setVisibility(View.GONE);
            return null;
        }
    }

    /**
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaPagamento.this);
        builder.setCancelable(false);
        builder.setMessage("Deseja cancelar a operação?\nTodas as informações serão perdidas!");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GettersSetters.setCondPgto(condicaoPgto);
                GettersSetters.setCodCondPgto(codCondPgto);
                GettersSetters.setDescrFormaPgto(descrFormaPgto);

                ColetaPagamento.this.finish();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaCondPgto extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ColetaPagamento.this);
            mProgressDialog.setIcon(R.drawable.ampulheta);
            mProgressDialog.setTitle("Condições de Pagamento");
            mProgressDialog.setMessage("Atualizando Condições de Pagamento... Aguarde...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Boolean doInBackground(Boolean... params) {
            ConexaoBDExt conecta = new ConexaoBDExt();
            conecta.selectCondPagto("0", "", true, ColetaPagamento.this);
            conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CONDPG_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
            return true;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(Boolean continua) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            radioGruopFormasPgto.clearCheck();
            spnCondPgto.setAdapter(null);
            spnCondPgto.setVisibility(View.GONE);
            descrFormaPgto = "";
            formaPgto = "";
            condicaoPgto = "";
            codCondPgto = "";
            arrcodCondPgto.clear();
            arrdescCondPgto.clear();
            arrformaCondPgto.clear();
            arrdescFormaCondPgto.clear();
            arrCondPgto.clear();
            avancar.setEnabled(true);
            llVencimentos.setVisibility(View.GONE);
            avancar.setVisibility(View.GONE);
        }
    }
}
