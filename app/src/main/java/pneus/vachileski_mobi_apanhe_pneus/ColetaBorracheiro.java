package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;
import pneus.vachileski_mobi_funcoes_genericas.ValidaCPFeCNPJ;

@SuppressWarnings({"SpellCheckingInspection", "DanglingJavadoc", "Convert2Lambda"})
@SuppressLint("StaticFieldLeak")
public class ColetaBorracheiro extends AppCompatActivity {
    Button btnAvancar, btnAssinar, btnVoltar, btnCancelar, btnBuscaBorrach, btnLimpaDadosBorracheiro;
    EditText cmpNomeBorr, cmpDocBorr, cmpDescrTipoColeta, cmpInfoAdic;
    RadioGroup rdGroupTipoColeta, rdGroupOrcamento;
    RadioButton rbReclamacao, rbRecapagem, rbProduto, rbPnNovo, rbServPatio, rbPnCasa, rbCarcacas, rbOutros, rbComOrcamento, rbSemOrcamento;
    LinearLayout llDadosBorracheiro, llOrcamento;
    CheckBox chkComissBorr, chkBorracheiro;

    static ImageView imgAssBorr;
    static View viewSnackBar;
    static LinearLayout llAssinaturaBorracheiro;

    ConexaoBDInt db;

    String docBorrach = "", descrTipoColeta = "", tipoColeta = "", nomeBorrach = "";
    boolean isBorrValid = false;
    static Bitmap bitmap = null;

    ArrayList<String> arrcodBorr = new ArrayList<>();
    ArrayList<String> arrnomeBorr = new ArrayList<>();
    ArrayList<String> arrdocBorr = new ArrayList<>();

    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_coleta_borracheiro);
        setTitle(getString(R.string.tituloColeta) + " - Borracheiro");

        db = new ConexaoBDInt(this);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnAvancar = findViewById(R.id.btnAvancar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnAssinar = findViewById(R.id.btnAssinar);
        chkBorracheiro = findViewById(R.id.chkBorracheiro);
        chkComissBorr = findViewById(R.id.chkComissBorr);
        imgAssBorr = findViewById(R.id.imgAssBorr);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        cmpNomeBorr = findViewById(R.id.cmpNomeBorr);
        cmpDocBorr = findViewById(R.id.cmpDocBorr);
        cmpInfoAdic = findViewById(R.id.cmpInfoAdic);
        cmpDescrTipoColeta = findViewById(R.id.cmpDescrTipoColeta);

        rdGroupTipoColeta = findViewById(R.id.rdGroupTipoColeta);
        rbReclamacao = findViewById(R.id.rbReclamacao);
        rbRecapagem = findViewById(R.id.rbRecapagem);
        rbProduto = findViewById(R.id.rbProduto);
        rbPnNovo = findViewById(R.id.rbPnNovo);
        rbServPatio = findViewById(R.id.rbServPatio);
        rbPnCasa = findViewById(R.id.rbPnCasa);
        rbCarcacas = findViewById(R.id.rbCarcacas);
        rbOutros = findViewById(R.id.rbOutros);

        llOrcamento = findViewById(R.id.llOrcamento);
        rdGroupOrcamento = findViewById(R.id.rdGroupOrcamento);
        rbComOrcamento = findViewById(R.id.rbComOrcamento);
        rbSemOrcamento = findViewById(R.id.rbSemOrcamento);

        llDadosBorracheiro = findViewById(R.id.llDadosBorracheiro);
        llAssinaturaBorracheiro = findViewById(R.id.llAssinaturaBorracheiro);
        btnBuscaBorrach = findViewById(R.id.btnBuscaBorrach);
        btnLimpaDadosBorracheiro = findViewById(R.id.btnLimpaDadosBorracheiro);

        chkBorracheiro.setVisibility(View.GONE);
        chkComissBorr.setVisibility(View.GONE);
        llAssinaturaBorracheiro.setVisibility(View.GONE);
        btnLimpaDadosBorracheiro.setVisibility(View.GONE);
        cmpDescrTipoColeta.setVisibility(View.GONE);
        llDadosBorracheiro.setVisibility(View.GONE);

        if (GettersSetters.getTipoVendedor().equals("P") && GettersSetters.getCodBorrachPatio().equals("")) {
            Snackbar.make(viewSnackBar, "Não é permitido informar BORRACHEIRO para VENDEDORES PÁTIO!", Snackbar.LENGTH_LONG).show();
            limpaBorracheiros();
        } else if (GettersSetters.getCategoriaCli().equals("01")) {
            Snackbar.make(viewSnackBar, "Não é permitido informar BORRACHEIRO para ÓRGÃOS PÚBLICOS!", Snackbar.LENGTH_LONG).show();
            limpaBorracheiros();
        } else if (GettersSetters.isClienteGrp()) {
            Snackbar.make(viewSnackBar, "Não é permitido informar BORRACHEIRO para CLIENTES DO GRUPO!", Snackbar.LENGTH_LONG).show();
            limpaBorracheiros();
            llOrcamento.setVisibility(View.GONE);
        } else {
            setAssinaturaBorr();
        }

        chkBorracheiro.setChecked(GettersSetters.isIsBorracheiro() || GettersSetters.isIsBorracheiroCli());

        rdGroupTipoColeta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void onCheckedChanged(final RadioGroup group, int rdGroupTipoColeta) {
                final RadioButton rbTipoColeta = group.findViewById(rdGroupTipoColeta);

                cmpDescrTipoColeta.setVisibility(View.GONE);
                chkBorracheiro.setVisibility(View.GONE);
                llDadosBorracheiro.setVisibility(View.GONE);

                if (llDadosBorracheiro.getVisibility() == View.GONE) {
                    chkBorracheiro.setChecked(false);
                }

                if (null != rbTipoColeta && rdGroupTipoColeta > -1) {
                    cmpInfoAdic.getText().clear();

                    if (rbReclamacao.isChecked()) {
                        descrTipoColeta = "RECLAMACAO";
                        cmpInfoAdic.setText(descrTipoColeta);

                        validaBorracheiroPatio();

                        /** VALIDAÇÃO QUANDO RECLAMAÇÃO PARA SELECIONAR O TIPO DO PNEU **/
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.interrogacao, null));
                        builder.setCancelable(false);
                        builder.setTitle("Qual o TIPO DO PNEU de RECLAMAÇÃO?");
                        builder.setPositiveButton("Pneus da Casa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tipoColeta = "'PC'";
                                chkBorracheiro.setVisibility(View.GONE);
                                pneuCasa(group);
                            }
                        });
                        builder.setNegativeButton("Recap/Vulc", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tipoColeta = "'SE'";
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } else if (rbRecapagem.isChecked()) {
                        descrTipoColeta = "RECAPAGEM";
                        tipoColeta = "'SE'";
                        validaBorracheiroPatio();
                    } else if (rbProduto.isChecked()) {
                        descrTipoColeta = "PRODUTO";
                        tipoColeta = "";
                    } else if (rbPnNovo.isChecked()) {
                        descrTipoColeta = "PNEU NOVO";
                        tipoColeta = "'PR'";
                    } else if (rbServPatio.isChecked()) {
                        descrTipoColeta = "SERVIÇO DE PÁTIO";
                        tipoColeta = "'SP'";
                    } else if (rbPnCasa.isChecked()) {
                        descrTipoColeta = "PNEU DA CASA";
                        tipoColeta = "'PC'";

                        pneuCasa(group);

                    } else if (rbCarcacas.isChecked()) {
                        descrTipoColeta = "CARCACAS";
                        tipoColeta = "'CA'";
                    } else if (rbOutros.isChecked()) {
                        tipoColeta = "";
                        cmpDescrTipoColeta.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(viewSnackBar, "Selecione o Tipo de Coleta!", Snackbar.LENGTH_LONG).show();
                        descrTipoColeta = "";
                        tipoColeta = "";
                    }
                }
            }
        });

        chkBorracheiro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (rbReclamacao.isChecked()) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                        builder.setCancelable(false);
                        builder.setTitle("Atenção");
                        builder.setMessage("Tipo RECLAMAÇÃO não permite Comissão de Borracheiro!");
                        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                limpaBorracheiros();
                                chkBorracheiro.setVisibility(View.GONE);
                            }
                        });
                        builder.show();
                    } else {
                        llDadosBorracheiro.setVisibility(View.VISIBLE);

                        //ALTERAÇÃO CMD 959
                        if (GettersSetters.isIsBorracheiroCli()) {
                            btnBuscaBorrach.setVisibility(View.GONE);
                            if (!GettersSetters.getCodBorracheiro().equals("S")) {
                                GettersSetters.setNomeBorracheiro("");
                                GettersSetters.setDocBorracheiro("");
                                chkBorracheiro.setVisibility(View.VISIBLE);
                                chkBorracheiro.setChecked(true);
                                llDadosBorracheiro.setVisibility(View.VISIBLE);

                                BuscaBorrach buscaBorrach = new BuscaBorrach();
                                buscaBorrach.execute();
                            }
                        }
                    }
                } else {
                    if (!GettersSetters.isIsBorracheiroCli()) {
                        limpaBorracheiros();
                    }
                    llDadosBorracheiro.setVisibility(View.GONE);
                }
            }
        });

        btnLimpaDadosBorracheiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                builder.setTitle("Confirma limpeza dos dados do Borracheiro?");
                builder.setIcon(R.drawable.exclamation);
                builder.setCancelable(false);
                builder.setMessage("Escolhendo essa opção TODOS os dados de Borracheiro, para ESTA COLETA, serão LIMPOS.\nPara outro borracheiro realizar uma nova busca!");
                builder.setPositiveButton("Limpar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        limpaBorracheiros();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnBuscaBorrach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GettersSetters.setCodBorracheiro("");
                GettersSetters.setNomeBorracheiro("");
                GettersSetters.setDocBorracheiro("");
                nomeBorrach = cmpNomeBorr.getText().toString();
                docBorrach = cmpDocBorr.getText().toString();

                arrcodBorr.clear();
                arrnomeBorr.clear();
                arrdocBorr.clear();

                if (!docBorrach.isEmpty() && !ValidaCPFeCNPJ.isDocValid(docBorrach)) {
                    cmpDocBorr.setError(getString(R.string.erro_documento_invalido));
                } else {
                    BuscaBorrach buscaBorrach = new BuscaBorrach();
                    buscaBorrach.execute();
                }
            }
        });

        btnAssinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GettersSetters.setTipoAssinatura("BORR");
                Intent i = new Intent(ColetaBorracheiro.this, Assinatura.class);
                startActivity(i);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                builder.setCancelable(true);
                builder.setMessage("Deseja cancelar o processo?\nTodas as informações serão perdidas!");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        Intent it = new Intent(ColetaBorracheiro.this, Home.class);
                        startActivity(it);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });

        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cmpDocBorr.getText().toString().trim().isEmpty() && cmpNomeBorr.getText().toString().trim().isEmpty() && chkBorracheiro.isChecked()) {
                    Snackbar.make(viewSnackBar, "Favor preencher os dados do Borracheiro!", Snackbar.LENGTH_LONG).show();
                } else if (!cmpDocBorr.getText().toString().trim().isEmpty() && chkBorracheiro.isChecked() && !ValidaCPFeCNPJ.isDocValid(cmpDocBorr.getText().toString())
                        && !GettersSetters.isIsBorracheiroVendPat() && !GettersSetters.isIsBorracheiroCli()) {
                    cmpDocBorr.setError(getString(R.string.erro_documento_invalido));
                } else if (cmpDocBorr.getText().toString().trim().isEmpty() && !cmpNomeBorr.getText().toString().trim().isEmpty() && chkBorracheiro.isChecked() && !isBorrValid) {
                    cmpDocBorr.setError(getString(R.string.erro_campo_obrigatorio));
                } else if (!cmpDocBorr.getText().toString().trim().isEmpty() && cmpNomeBorr.getText().toString().trim().isEmpty() && chkBorracheiro.isChecked() && !isBorrValid) {
                    cmpNomeBorr.setError(getString(R.string.erro_campo_obrigatorio));
                } else if (descrTipoColeta.equals("") && !rbOutros.isChecked()) {
                    Snackbar.make(viewSnackBar, "Selecione o Tipo de Coleta!", Snackbar.LENGTH_LONG).show();
                    chkBorracheiro.setVisibility(View.GONE);
                } else if (rbReclamacao.isChecked() && cmpInfoAdic.getText().toString().trim().isEmpty()) {
                    Snackbar.make(viewSnackBar, "Para o tipo reclamação preencher Informações Adicionais!", Snackbar.LENGTH_LONG).show();
                    cmpInfoAdic.setError(getString(R.string.erro_campo_obrigatorio));
                } else if (rbOutros.isChecked() && cmpDescrTipoColeta.getText().toString().trim().isEmpty()) {
                    Snackbar.make(viewSnackBar, "Favor preencher a Descrição do Tipo de Coleta", Snackbar.LENGTH_LONG).show();
                    cmpDescrTipoColeta.setError(getString(R.string.erro_campo_obrigatorio));
                } else if ((!cmpNomeBorr.getText().toString().trim().isEmpty() || !cmpDocBorr.getText().toString().trim().isEmpty()) && !isBorrValid && chkBorracheiro.isChecked()) {
                    Snackbar.make(viewSnackBar, "Valide os dados do Borracheiro clicando na Lupa!", Snackbar.LENGTH_LONG).show();
                } else if (bitmap == null && chkBorracheiro.isChecked() && isBorrValid) {
                    Snackbar.make(viewSnackBar, "Sem assinatura do Borracheiro", Snackbar.LENGTH_LONG).setAction("Assinar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GettersSetters.setTipoAssinatura("BORR");

                            Intent it = new Intent(ColetaBorracheiro.this, Assinatura.class);
                            startActivity(it);
                        }
                    }).show();
                } else if (!GettersSetters.isClienteGrp() && !rbComOrcamento.isChecked() && !rbSemOrcamento.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                    builder.setTitle("Orçamento");
                    builder.setMessage("Coleta possui Orçamento?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rbSemOrcamento.setChecked(true);
                        }
                    });
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rbComOrcamento.setChecked(true);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    btnAvancar.setEnabled(false);

                    GettersSetters.setIsOrcamento(rbComOrcamento.isChecked());

                    if (chkBorracheiro.isChecked()) {
                        GettersSetters.setIsBorracheiro(true);
                        GettersSetters.setNomeBorracheiro(cmpNomeBorr.getText().toString());
                        GettersSetters.setDocBorracheiro(cmpDocBorr.getText().toString());
                    } else {
                        GettersSetters.setIsBorracheiro(false);
                        GettersSetters.setNomeBorracheiro("");
                        GettersSetters.setDocBorracheiro("");
                        chkComissBorr.setChecked(false);
                        GettersSetters.setCheckComissBorr(false);
                        GettersSetters.setCodBorracheiro("");
                        GettersSetters.setPathAssinBorr(null);
                        GettersSetters.setPicNameAssinBorr(null);
                    }

                    GettersSetters.setTipoColeta(tipoColeta);
                    if (cmpInfoAdic.getText().toString().length() > 0) {
                        GettersSetters.setInfoAdicionais(cmpInfoAdic.getText().toString());
                    }
                    GettersSetters.setDescrTipoColeta(descrTipoColeta);

                    if (rbOutros.isChecked()) {
                        descrTipoColeta = cmpDescrTipoColeta.getText().toString();
                        GettersSetters.setDescrTipoColeta(tipoColeta);
                    }

                    if (chkComissBorr.isChecked()) {
                        if (rbReclamacao.isChecked()) {
                            chkComissBorr.setChecked(false);
                            GettersSetters.setCheckComissBorr(false);
                        } else {
                            GettersSetters.setCheckComissBorr(true);
                        }
                    } else {
                        GettersSetters.setCheckComissBorr(false);
                    }

                    if (!chkBorracheiro.isChecked() && chkBorracheiro.getVisibility() == View.VISIBLE) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                        builder.setCancelable(false);
                        builder.setTitle("Sem Borracheiro!");
                        builder.setMessage("Borracheiro NÃO informado.\nDeseja informar?");
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                chkBorracheiro.setChecked(true);
                                btnAvancar.setEnabled(true);
                            }
                        });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                limpaBorracheiros();

                                Intent it = new Intent(ColetaBorracheiro.this, ColetaItens.class);
                                startActivity(it);
                            }
                        });
                        builder.show();
                    } else {
                        Intent it = new Intent(ColetaBorracheiro.this, ColetaItens.class);
                        startActivity(it);
                    }

                    btnAvancar.setEnabled(true);
                }
            }
        });
    }

    public void limpaBorracheiros() {
        chkComissBorr.setVisibility(View.GONE);
        llAssinaturaBorracheiro.setVisibility(View.GONE);
        btnLimpaDadosBorracheiro.setVisibility(View.GONE);
        btnBuscaBorrach.setVisibility(View.VISIBLE);

        arrcodBorr.clear();
        arrnomeBorr.clear();
        arrdocBorr.clear();

        nomeBorrach = "";
        docBorrach = "";
        cmpNomeBorr.getText().clear();
        cmpDocBorr.getText().clear();
        cmpDocBorr.setEnabled(true);
        cmpNomeBorr.setEnabled(true);

        chkBorracheiro.setChecked(false);

        GettersSetters.setCodBorracheiro("");
        GettersSetters.setNomeBorracheiro("");
        GettersSetters.setDocBorracheiro("");
        GettersSetters.setIsBorracheiroCli(false);
        GettersSetters.setIsBorracheiro(false);

        isBorrValid = false;
        if (GettersSetters.getPathAssinBorr() != null) {
            File fileAssBorr = new File(GettersSetters.getPathAssinBorr(), GettersSetters.getPicNameAssinBorr());
            @SuppressWarnings("unused")
            boolean deletedAssBorr = fileAssBorr.delete();

            GettersSetters.setPathAssinBorr(null);
            GettersSetters.setPicNameAssinBorr(null);
            imgAssBorr.setImageDrawable(null);
            imgAssBorr.setImageBitmap(null);
            imgAssBorr.setBackground(null);
            bitmap = null;
            llAssinaturaBorracheiro.setVisibility(View.GONE);
            imgAssBorr.setVisibility(View.GONE);
        }
    }

    public static void setAssinaturaBorr() {
        try {
            String image_path = GettersSetters.getPathAssinBorr() + GettersSetters.getPicNameAssinBorr(); //getIntent().getStringExtra("imagePath");
            bitmap = BitmapFactory.decodeFile(image_path);
            if (bitmap == null) {
                imgAssBorr.setVisibility(View.GONE);
                llAssinaturaBorracheiro.setVisibility(View.GONE);
            } else {
                llAssinaturaBorracheiro.setVisibility(View.VISIBLE);
                imgAssBorr.setVisibility(View.VISIBLE);
                imgAssBorr.setImageBitmap(bitmap);
            }
        } catch (Exception err) {
            Snackbar.make(viewSnackBar, "Erro ao salvar assinatura!", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
        builder.setCancelable(true);
        builder.setMessage("Deseja cancelar a operação?\nTodas as informações serão perdidas!");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (GettersSetters.getPathAssinBorr() != null) {
                    File fileAssBorr = new File(GettersSetters.getPathAssinBorr() + "/", GettersSetters.getPicNameAssinBorr());
                    @SuppressWarnings("unused")
                    boolean deletedAssBorr = fileAssBorr.delete();
                }
                imgAssBorr.setImageDrawable(null);
                finish();
                GettersSetters.setPathAssinBorr(null);
                GettersSetters.setPicNameAssinBorr(null);
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("StaticFieldLeak")
    public class BuscaBorrach extends AsyncTask<ArrayList, String, ArrayList> {
        AlertDialog dialogBuscaBorrach = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Borracheiros...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBuscaBorrach = builder.create();
            dialogBuscaBorrach.show();
        }

        @Override
        protected ArrayList doInBackground(ArrayList... params) {
            Cursor buscaBorrachLocal;

            if (GettersSetters.isIsBorracheiro()) {
                buscaBorrachLocal = db.selectBorracheiro("", "", GettersSetters.getCodBorracheiro());
            } else {
                if (!nomeBorrach.isEmpty()) {
                    buscaBorrachLocal = db.selectBorracheiro(nomeBorrach.replace(" ", "%"), "", "");
                } else {
                    buscaBorrachLocal = db.selectBorracheiro("", docBorrach, "");
                }
            }

            /** ALIMENTA OS ARRAYS COM OS DADOS RETORNADOS DOS BANCOS DE DADOS **/
            if (buscaBorrachLocal != null) {
                if (buscaBorrachLocal.getCount() > 0) {
                    arrcodBorr = db.arrcodBorr;
                    arrnomeBorr = db.arrnomeBorr;
                    arrdocBorr = db.arrdocBorr;
                }
            }

            return arrnomeBorr;
        }

        @Override
        protected void onPostExecute(ArrayList itens) {
            if (dialogBuscaBorrach != null) {
                dialogBuscaBorrach.dismiss();
            }

            if (itens.size() > 0) {
                buscaBorracheiro();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                builder.setTitle("Nenhum Boracheiro encontrado!");
                builder.setMessage("Continuar com estes dados? " + cmpDocBorr.getText() + "-" + cmpNomeBorr.getText());
                builder.setIcon(R.drawable.exclamation);
                builder.setCancelable(false);
                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GettersSetters.setCodBorracheiro("ZZZZZZ");
                        chkComissBorr.setVisibility(View.VISIBLE);
                        chkComissBorr.setChecked(true);
                        llAssinaturaBorracheiro.setVisibility(View.VISIBLE);
                        isBorrValid = !cmpDocBorr.getText().toString().trim().isEmpty() && !cmpNomeBorr.getText().toString().trim().isEmpty();
                        cmpDocBorr.setEnabled(!isBorrValid);
                        cmpNomeBorr.setEnabled(!isBorrValid);
                        btnLimpaDadosBorracheiro.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Alterar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chkComissBorr.setVisibility(View.GONE);
                        llAssinaturaBorracheiro.setVisibility(View.GONE);
                        GettersSetters.setCodBorracheiro("");
                        GettersSetters.setNomeBorracheiro("");
                        isBorrValid = false;
                        if (GettersSetters.getPathAssinBorr() != null) {
                            File fileAssBorr = new File(GettersSetters.getPathAssinBorr(), GettersSetters.getPicNameAssinBorr());
                            @SuppressWarnings("unused")
                            boolean deletedAssBorr = fileAssBorr.delete();

                            GettersSetters.setPathAssinBorr(null);
                            GettersSetters.setPicNameAssinBorr(null);
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            Log.i("AsyncTask", "Tirando dialog da tela Thread: " + Thread.currentThread().getName());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void pneuCasa(final RadioGroup group) {
        /** ATUALIZAÇÃO DA FILIAL QUANDO PNEUS DA CASA **/
        final String filialPneusCasa = "080101";
        final String descrFilialPneusCasa = "VLK PNEUS ESPECIAIS";

        if (!GettersSetters.getFilial().trim().equals("080101") &&
                !GettersSetters.getFilial().trim().startsWith("1101") && !GettersSetters.getFilial().trim().startsWith("1201") && !GettersSetters.getFilial().trim().startsWith("1301") &&
                !GettersSetters.getFilial().trim().equals("020108") && !GettersSetters.getFilial().trim().equals("020111")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
            builder.setCancelable(false);
            builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
            builder.setTitle("A_T_E_N_Ç_Ã_O Filial Alterada!");
            builder.setMessage("Para tipo de Coleta [" + descrTipoColeta + "] Filial será alterada para\n[" + filialPneusCasa + " - " + descrFilialPneusCasa + "]");
            builder.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GettersSetters.setFilial(filialPneusCasa);
                    GettersSetters.setDescrFilial(descrFilialPneusCasa);
                }
            });
            builder.setNegativeButton("Mudar Tipo Coleta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    group.clearCheck();
                    descrTipoColeta = "";
                    tipoColeta = "";
                    cmpDescrTipoColeta.setVisibility(View.GONE);
                    chkBorracheiro.setVisibility(View.GONE);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void buscaBorracheiro() {
        final String[] codigoBorracheiroLocalizado = new String[arrcodBorr.size()];
        final String[] nomeBorracheiroLocalizado = new String[arrnomeBorr.size()];
        final String[] docBorracheiroLocalizado = new String[arrdocBorr.size()];
        final String[] dadosBorracheiro = new String[arrnomeBorr.size()];
        for (int i = 0; i < arrnomeBorr.size(); i++) {
            String documento = arrdocBorr.get(i);
            if (documento.trim().length() == 14) {
                documento = documento.trim().replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
            } else {
                documento = documento.trim().replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
            }

            codigoBorracheiroLocalizado[i] = arrcodBorr.get(i);
            nomeBorracheiroLocalizado[i] = arrnomeBorr.get(i);
            docBorracheiroLocalizado[i] = documento;
            dadosBorracheiro[i] = arrnomeBorr.get(i) + "\n" + documento;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
        if (GettersSetters.isIsBorracheiroCli()) {
            builder.setTitle("Borrach. vinculado ao Cliente");
            builder.setCancelable(true);
        } else if (GettersSetters.isIsBorracheiroVendPat()) {
            builder.setTitle("Borrach. Vend. Pátio");
            builder.setCancelable(false);
        } else {
            builder.setTitle("Selecione o Borracheiro");
            builder.setCancelable(true);
        }
        builder.setIcon(R.drawable.ferramentas);

        final ArrayAdapter<String> adapterBorracheiros = new ArrayAdapter<String>(ColetaBorracheiro.this, android.R.layout.simple_expandable_list_item_1, dadosBorracheiro) {
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextSize(14);
                return view;
            }
        };

        builder.setAdapter(adapterBorracheiros, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemSelecionado) {
                cmpNomeBorr.setText(nomeBorracheiroLocalizado[itemSelecionado]);
                cmpDocBorr.setText(docBorracheiroLocalizado[itemSelecionado].trim().replace(".", "").replace("-", "").replace("/", ""));
                GettersSetters.setCodBorracheiro(codigoBorracheiroLocalizado[itemSelecionado]);

                chkComissBorr.setChecked(true);
                chkComissBorr.setVisibility(View.VISIBLE);
                chkBorracheiro.setEnabled(!GettersSetters.isIsBorracheiroVendPat());

                llAssinaturaBorracheiro.setVisibility(View.VISIBLE);
                isBorrValid = true;
                cmpDocBorr.setEnabled(false);
                cmpDocBorr.setFocusableInTouchMode(true);
                cmpNomeBorr.setEnabled(false);
                if (GettersSetters.isIsBorracheiroVendPat()) {
                    btnLimpaDadosBorracheiro.setVisibility(View.GONE);
                    btnBuscaBorrach.setVisibility(View.GONE);
                } else {
                    btnLimpaDadosBorracheiro.setVisibility(View.VISIBLE);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void validaBorracheiroPatio() {
        if (!GettersSetters.getCategoriaCli().equals("01") && !GettersSetters.isClienteGrp()) {
            chkBorracheiro.setVisibility(View.VISIBLE);

            if (GettersSetters.isIsBorracheiroCli()) {
                chkBorracheiro.setChecked(true);
            }

            if (GettersSetters.getTipoVendedor().equals("P")) {
                if (!GettersSetters.getCodBorrachPatio().equals("")) { //QUANDO S PERMITE SELECIONAR O BORRACHEIRO
                    if (!GettersSetters.getCodBorrachPatio().equals("S")) {
                        Cursor cursor = db.selectBorracheiro("", "", GettersSetters.getCodBorrachPatio());
                        if (cursor.getCount() > 0) {
                            chkBorracheiro.setChecked(true);

                            arrcodBorr = db.arrcodBorr;
                            arrnomeBorr = db.arrnomeBorr;
                            arrdocBorr = db.arrdocBorr;

                            GettersSetters.setIsBorracheiroVendPat(true);
                            buscaBorracheiro();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBorracheiro.this);
                            builder.setCancelable(false);
                            builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                            builder.setTitle("Sem borracheiro");
                            builder.setMessage("Nenhum borracheiro localizado. Favor realizar a ATUALIZAÇÃO dos BORRACHEIROS. Após atualizar, inicie novamente a Coleta");
                            builder.setPositiveButton("Fechar", null);
                            builder.show();
                        }
                    }
                } else {
                    chkBorracheiro.setVisibility(View.GONE);
                }
            }

            if (chkBorracheiro.isChecked()) {
                llDadosBorracheiro.setVisibility(View.VISIBLE);
            }
        }
    }
}
