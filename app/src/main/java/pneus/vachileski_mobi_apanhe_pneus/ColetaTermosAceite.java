package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

public class ColetaTermosAceite extends AppCompatActivity {

    Button aceitar;
    //Button voltar;

    CheckBox aceite1;
    CheckBox aceite2;
    CheckBox aceite3;

    View viewAccTermos;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_coleta_termos_aceite);
        setTitle(getString(R.string.tituloColeta) + " - Termos de Compromisso");

        aceitar = findViewById(R.id.btnAvancar);
        //voltar  = findViewById(R.id.btnVoltar);

        aceite1 = findViewById(R.id.chkAceite1);
        aceite2 = findViewById(R.id.chkAceite2);
        aceite3 = findViewById(R.id.chkAceite3);

        viewAccTermos = findViewById(R.id.viewAccTermos);

        if (GettersSetters.isAceiteTermos()) {
            aceite1.setChecked(true);
            aceite2.setChecked(true);
            aceite3.setChecked(true);
        }

        if (aceite1.isChecked()) {
            aceite1.setText("Aceito");
            aceite1.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            aceite1.setText("Não Aceito");
            aceite1.setTextColor(getResources().getColor(R.color.red));
        }

        if (aceite2.isChecked()) {
            aceite2.setText("Aceito");
            aceite2.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            aceite2.setText("Não Aceito");
            aceite2.setTextColor(getResources().getColor(R.color.red));
        }

        if (aceite3.isChecked()) {
            aceite3.setText("Aceito");
            aceite3.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            aceite3.setText("Não Aceito");
            aceite3.setTextColor(getResources().getColor(R.color.red));
        }

        aceite1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aceite1.isChecked()) {
                    aceite1.setText("Aceito");
                    aceite1.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    aceite1.setText("Não Aceito");
                    aceite1.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        aceite2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aceite2.isChecked()) {
                    aceite2.setText("Aceito");
                    aceite2.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    aceite2.setText("Não Aceito");
                    aceite2.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        aceite3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aceite3.isChecked()) {
                    aceite3.setText("Aceito");
                    aceite3.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    aceite3.setText("Não Aceito");
                    aceite3.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        aceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aceite1.isChecked() || !aceite2.isChecked() || !aceite3.isChecked()) {
                    GettersSetters.setAceiteTermos(false);
                    Snackbar.make(viewAccTermos, "LER E ACEITAR TODOS OS TERMOS!", Snackbar.LENGTH_LONG).show();
//                            setAction("Aceitar", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            aceite1.setChecked(true);
//                            aceite2.setChecked(true);
//                            aceite3.setChecked(true);
//                            GettersSetters.setAceiteTermos(true);
//                            finish();
//                            ColetaConclusao.aceiteTermos();
//                        }
//                    }).show();
                } else {
                    //GettersSetters.setValidarDadosCliente(true);
                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaTermosAceite.this);
                    builderFilial.setTitle("Confirmo o aceite dos termos");
                    builderFilial.setIcon(R.drawable.success);
                    builderFilial.setCancelable(false);
                    builderFilial.setMessage("Eu, " + GettersSetters.getRzSocialCli().trim() + " confirmo que li e aceito todos os termos propostos.");
                    builderFilial.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GettersSetters.setAceiteTermos(true);
                            finish();
                            //ColetaConclusao.aceiteTermos();
                        }
                    });
                    builderFilial.setNegativeButton("Reler", null);
                    AlertDialog dialog = builderFilial.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!aceite1.isChecked() || !aceite2.isChecked() || !aceite3.isChecked()) {
            GettersSetters.setAceiteTermos(false);
        }
        finish();
        //ColetaConclusao.aceiteTermos();
    }
}
