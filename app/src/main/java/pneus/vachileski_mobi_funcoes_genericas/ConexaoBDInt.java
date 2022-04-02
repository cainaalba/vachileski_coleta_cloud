package pneus.vachileski_mobi_funcoes_genericas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection", "UnusedReturnValue"})
public class ConexaoBDInt extends SQLiteOpenHelper {
    public static final String NOME_BANCO = "VachileskiColeta.db";

    /*
     * VERSÃO DE CONTROLE PARA ATUALIZAÇÃO DO SQLITE
     * QUANDO NECESSÁRIO ATUALIZAR, DEVE-SE ENVIAR A VERSÃO ATUAL + 1
     **/
    public static int VERSAO = 1;
    private boolean resultado = false;
    private String query = "";

    /*
     * TABELA FILIAIS
     **/
    private static final String FILIAIS = "FILIAIS";
    private static final String ID_EMP = "ID_EMP";
    private static final String ID_FILIAL = "ID_FILIAL";
    private static final String CD_AUXILIAR = "CD_AUXILIAR";
    private static final String RZ_SOCIAL = "RZ_SOCIAL";
    private static final String NOME_FANT = "NOME_FANT";

    //Produto
    public String codProd = "";
    public String descrProd = "";
    public String prdBlq = "";
    public String descDesenho = "";
    public String precoTabela = "";
    public String descontVachiOst = "";
    public String descontLauxen = "";

    public String descrFilial = "";

    public ArrayList<String> arrIdUsuario = new ArrayList<>();
    public ArrayList<String> arrUsuario = new ArrayList<>();
    public ArrayList<String> arrCodTotvs = new ArrayList<>();
    public ArrayList<String> arrNomeTotvs = new ArrayList<>();
    public ArrayList<String> arrTipovend = new ArrayList<>();
    public ArrayList<String> arrCliPermitidos = new ArrayList<>(); //CLIENTES PERMITIDOS PARA BUSCA
    public ArrayList<String> arrCodBorrachPat = new ArrayList<>();

    //ARRAY DOS PRODUTOS
    public ArrayList<String> arrCodProdutos = new ArrayList<>();
    public ArrayList<String> arrDescProdutos = new ArrayList<>();
    public ArrayList<String> arrProdBlq = new ArrayList<>();
    public ArrayList<String> arrDesenhoProd = new ArrayList<>();
    public ArrayList<String> arrPrecoTabela = new ArrayList<>();
    public ArrayList<String> arrDescVachiOst = new ArrayList<>();
    public ArrayList<String> arrDescLauxen = new ArrayList<>();
    public ArrayList<Boolean> arrIsDescGrp = new ArrayList<>();

    //    public ArrayList<String> arrDadosFilial = new ArrayList<>();
    public ArrayList<String> arrDadosFiliaisUsuarios = new ArrayList<>();
    public ArrayList<String> arrCodFilialUsuario = new ArrayList<>();
    public ArrayList<String> arrDescrFilialUsuario = new ArrayList<>();
    public ArrayList<String> arrEstados = new ArrayList<>();
//    public ArrayList<String> arrMunEst = new ArrayList<>();

    // Arrays de busca do cabeçalho da coleta
    public ArrayList<String> arrBuscaColetaFilial = new ArrayList<>();
    public ArrayList<String> arrBuscaNumeroColeta = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaData = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCodCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaLojaCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNomeCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCodVend = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaQtdItens = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaValor = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCodCondPg = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCondPg = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaFormaPg = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaInfoAdic = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCodBorr = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNomeBorr = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaDocumBorr = new ArrayList<>();
    public ArrayList<byte[]> arrBuscaColetaAssinBorr = new ArrayList<>();
    public ArrayList<byte[]> arrBuscaColetaAssinCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaStatus = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaStatusEmail = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaIdSeq = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaTipoColeta = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaHoraColeta = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaColPor = new ArrayList<>();
    public ArrayList<byte[]> arrBuscaColetaPdf = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaIdUsr = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaDocCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNomeVend = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaDtChegada = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaOrcamento = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaOffline = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaCliNovo = new ArrayList<>();

    //ARRAY DOS BORRACHEIROS
    public ArrayList<String> arrcodBorr = new ArrayList<>();
    public ArrayList<String> arrnomeBorr = new ArrayList<>();
    public ArrayList<String> arrdocBorr = new ArrayList<>();

    //DADOS DA COLETA
    public String buscaColetaFilial = "";
    public String buscaColetaNumero = "";
    public String buscaColetaData = "";
    public String buscaColetaCodCli = "";
    public String buscaColetaLojaCli = "";
    public String buscaColetaNomeCli = "";
    public String buscaColetaCodVend = "";
    public String buscaColetaQtdItens = "";
    public String buscaColetaValor = "";
    public String buscaColetaCodCondPg = "";
    public String buscaColetaCondPg = "";
    public String buscaColetaFormaPg = "";
    public String buscaColetaInfoAdic = "";
    public String buscaColetaCodBorr = "";
    public String buscaColetaNomeBorr = "";
    public String buscaColetaDocBorr = "";
    public byte[] buscaColetaAssinBorr = null;
    public byte[] buscaColetaAssinCli = null;
    public String buscaColetaStatus = "";
    public String buscaColetaStatusEmail = "";
    public String buscaColetaIdSeq = "";
    public String buscaColetaTipoCol = "";
    public String buscaColetaColetPor = "";
    public String buscaColetaHora = "";
    public byte[] buscaColetaPDFByte = null;
    public String buscaColetaIdUsr = "";
    public String buscaColetaDocCli = "";
    public String buscaColetaDtChegada = "";
    public String buscaColetaNomeVend = "";
    public String buscaColetaOrcamento = "";

    // Arrays de busca dos itens da coleta
    public ArrayList<String> arrBuscaColetaItFilial = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItIdentif = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItItem = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItData = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItCodCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItLojaCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItQtd = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItVlrUnit = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItVlrTotal = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItCodPrd = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItBitola = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItMarca = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItModelo = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItSerie = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItDot = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItMontado = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItDesenho = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItUrgente = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItPercComBorr = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItVlrComBorr = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItStatus = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItObs = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItCAgua = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItCCamara = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaItGarantia = new ArrayList<>();

    //Variáveis busca coleta cli
    public String buscaColetaCliFilial = "";
    public String buscaColetaCliIdentif = "";
    public String buscaColetaCliData = "";
    public String buscaColetaCliNome = "";
    public String buscaColetaCliCodCli = "";
    public String buscaColetaCliLojali = "";
    public String buscaColetaCliDodCli = "";
    public String buscaColetaCliIe = "";
    public String buscaColetaCliEmail = "";
    public String buscaColetaCliEntEnd = "";
    public String buscaColetaCliEntBairro = "";
    public String buscaColetaCliEntMunic = "";
    public String buscaColetaCliEntEst = "";
    public String buscaColetaCliEntCep = "";
    public String buscaColetaCliEntDDD = "";
    public String buscaColetaCliEntFone = "";
    public String buscaColetaCliCobrEnd = "";
    public String buscaColetaCliCobrBairro = "";
    public String buscaColetaCliCobrMunic = "";
    public String buscaColetaCliCobrEst = "";
    public String buscaColetaCliCobrCep = "";
    public String buscaColetaCliCobrDDD = "";
    public String buscaColetaCliCobrFone = "";
    public String buscaColetaCliInfoAdic = "";
    public byte[] buscaColetaCliRG = new byte[0];
    public byte[] buscaColetaCliCPF = new byte[0];
    public byte[] buscaColetaCliCompResid = new byte[0];
    public String buscaColetaCliCategoria = "";

    //ARRAYS COM COMISSÃO DE BORRACHEIROS
    public ArrayList<String> arrIdComissBorr = new ArrayList<>();
    public ArrayList<String> arrFxIniComissBorr = new ArrayList<>();
    public ArrayList<String> arrFxFimComissBorr = new ArrayList<>();
    public ArrayList<Double> arrPercComissBorr = new ArrayList<>();

    //ARRAY DE CLIENTES
    public ArrayList<String> arrNomeCliente = new ArrayList<>();
    public ArrayList<String> arrDocCliente = new ArrayList<>();
    public ArrayList<String> arrMunCliente = new ArrayList<>();
    public ArrayList<String> arrEstCliente = new ArrayList<>();
    public ArrayList<String> arrIECliente = new ArrayList<>();
    public ArrayList<String> arrRecnoCliente = new ArrayList<>();

    //Cliente NE
    public String cgcCliente = "";
    public String nomeCli = "";
    public String endCli = "";
    public String bairroCli = "";
    public String cepCli = "";
    public String emailCli = "";
    public String munCli = "";
    public String ufCli = "";
    public String dddCli = "";
    public String foneCli = "";
    public String codCli = "";
    public String lojaCli = "";
    public String ieCli = "";
    public String endCobCli = "";
    public String bairroCobCli = "";
    public String cepCobCli = "";
    public String munCobCli = "";
    public String ufCobCli = "";
    public String ddd2Cli = "";
    public String telexCli = "";
    public String cliBlq = "";
    public String condPgtoCli = "";
    public String catCli = "";
    public String tabPrcCli = "";
    public boolean clienteGrp = false;
    public double descExclusivo = 0;

    //VARIAVEIS PARA GERAÇÃO DO IDENTIFICADOR DA COLETA
    public int identifSeq = 0;

    // Borracheiros
    public String codBorr = "";
    public String nomeBorr = "";
    public String docBorr = "";

    //ARRAY COM AS COLETAS NÃO ENVIADAS
    public ArrayList<String> arrBuscaColetaNaoEnviadaFilial = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviada = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaData = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaNomeCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaCodCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaLojaCli = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaCodVend = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaColPor = new ArrayList<>();
    public ArrayList<String> arrBuscaColetaNaoEnviadaCodCRecap = new ArrayList<>();

    public ArrayList<String> arrcodCondPgto = new ArrayList<>();
    public ArrayList<String> arrdescCondPgto = new ArrayList<>();
    public ArrayList<String> arrformaCondPgto = new ArrayList<>();
    public ArrayList<String> arrdescFormaCondPgto = new ArrayList<>();
    public ArrayList<String> arrCondPgto = new ArrayList<>();

    public ConexaoBDInt(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    /**
     * VALIDAÇÕES PARA TROCAS DE VERSÕES AUTOMATICAMENTE
     **/
    public int getVersion() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.getVersion();
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //criacao das tabelas do banco
        db.execSQL("CREATE TABLE USUARIOS (ID_USR TEXT PRIMARY KEY, NOME_USR TEXT, LOGIN_USR TEXT, PASSWD_USR TEXT, STATUS_USR TEXT, EMAIL_USR TXT, CPF_USR TXT, TIPO_USR TXT, COD_TOTVS TEXT, NOME_TOTVS TEXT)");
        db.execSQL("CREATE TABLE  USUARIOS_VENDEDORES (USRVEND_ID TEXT, USRVEND_NOME TEXT, USRVEND_COD_TOTVS TEXT, USRVEND_NOME_TOTVS TEXT, USRVEND_TIPO_VEND TEXT, USRVEND_GERENTE TEXT, USRVEND_ID_PERMITIDO TEXT, " +
                "USRVEND_CLIENTES_PERMITIDOS TEXT, USRVEND_COD_BORRACHEIRO TEXT)");
        db.execSQL("CREATE TABLE FILIAIS (ID_EMP TEXT, ID_FILIAL TEXT, CD_AUXILIAR TEXT, RZ_SOCIAL TEXT, NOME_FANT TEXT)");
        db.execSQL("CREATE TABLE FILIAIS_USUARIOS (EMPRESAID TEXT, FILIALID TEXT, CDAUXILIAR TEXT, USUARIOID TEXT, RZSOCIAL TEXT)");
        db.execSQL("CREATE TABLE ESTADOS (SIGLA TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE ESTADOS_MUNICIPIOS (SIGLA_EST TEXT, DESC_MUN TEXT)");
        db.execSQL("CREATE TABLE CEP_MUNICIPIOS (CEP TEXT PRIMARY KEY, LOGRADOURO TEXT, COMPLEMENTO TEXT, BAIRRO TEXT, LOCALIDADE TEXT, UF TEXT, IBGE TEXT)");
        db.execSQL("CREATE TABLE COLETA_CABEC (COL_CABEC_FILIAL TEXT, COL_CABEC_IDENTIF TEXT, COL_CABEC_DATA TEXT, COL_CABEC_COD_CLI TEXT, COL_CABEC_LOJA_CLI TEXT, " +
                "COL_CABEC_RZ_SOCIAL TEXT, COL_CABEC_VENDEDOR TEXT, COL_CABEC_QNT_ITENS TEXT, COL_CABEC_TOTAL TEXT, COL_CABEC_COD_COND_PG TEXT, COL_CABEC_COND_PG TEXT, COL_CABEC_FORMA_PG TEXT, COL_CABEC_INFO_ADIC TEXT, " +
                "COL_CABEC_BORRACHEIRO TEXT, COL_CABEC_NOME_BORRACHEIRO TEXT, COL_CABEC_DOC_BORRACHEIRO TEXT, COL_CABEC_ASSIN_BORR BLOB, COL_CABEC_ASSIN_CLI BLOB, COL_CABEC_STATUS_ENVIO TEXT, " +
                "COL_CABEC_STATUS_ENVIO_EMAIL TEXT, COL_CABEC_SEQ_IDENTIFICADOR TEXT, COL_CABEC_TIPO_COLETA TEXT, COL_CABEC_HORA_COLETA TEXT, COL_CABEC_COL_POR TEXT, COL_CABEC_PDF TEXT, COL_CABEC_ID_USR TEXT, " +
                "COL_CABEC_DOC_CLI TEXT, COL_CABEC_DT_CHEGADA TEXT, COL_CABEC_ORCAMENTO TEXT, COL_CABEC_OFFLINE TEXT, COL_CABEC_CLI_NOVO TEXT, PRIMARY KEY(COL_CABEC_FILIAL,COL_CABEC_IDENTIF))");
        db.execSQL("CREATE TABLE COLETA_CLI (COL_CLI_FILIAL TEXT, COL_CLI_IDENTIF TEXT PRIMARY KEY, COL_CLI_DATA TEXT, COL_CLI_COD_CLI TEXT, COL_CLI_LOJA_CLI TEXT, " +
                "COL_CLI_DOC_CLI TEXT, COL_CLI_IE TEXT, COL_CLI_EMAIL TEXT, COL_CLI_ENT_END TEXT, COL_CLI_ENT_BAIRRO TEXT, " +
                "COL_CLI_ENT_MUNIC TEXT, COL_CLI_ENT_EST TEXT, COL_CLI_ENT_CEP TEXT, COL_CLI_ENT_DDD TEXT, COL_CLI_ENT_FONE TEXT, COL_CLI_COBR_END TEXT, " +
                "COL_CLI_COBR_BAIRRO TEXT, COL_CLI_COBR_MUNIC TEXT, COL_CLI_COBR_EST TEXT, COL_CLI_COBR_CEP TEXT, COL_CLI_COBR_DDD TEXT, COL_CLI_COBR_FONE TEXT, " +
                "COL_CLI_STATUS_ENVIO TEXT, COL_CLI_INFO_ADIC TEXT, COL_CLI_RG BLOB, COL_CLI_CPF BLOB, COL_CLI_COMP_RESID BLOB, COL_CLI_NOME TEXT, COL_CLI_CATEGORIA TEXT)");
        db.execSQL("CREATE TABLE COLETA_ITENS (COL_IT_FILIAL TEXT, COL_IT_IDENTIF TEXT, COL_IT_ITEM TEXT, COL_IT_DATA TEXT, COL_IT_COD_CLI TEXT, COL_IT_LOJA_CLI TEXT, COL_IT_QTD TEXT, COL_IT_VALOR_UNIT TEXT, " +
                "COL_IT_VALOR TEXT, COL_IT_COD_PROD TEXT, COL_IT_BITOLA TEXT, COL_IT_MARCA TEXT, COL_IT_MODELO TEXT, COL_IT_SERIE TEXT, COL_IT_DOT TEXT, COL_IT_MONTADO TEXT, COL_IT_DESENHO TEXT, " +
                "COL_IT_URGENTE TEXT, COL_IT_PERC_COM_BORR TEXT, COL_IT_VLR_COM_BORR TEXT, COL_IT_STATUS_ENVIO TEXT, COL_IT_OBS TEXT, COL_IT_C_AGUA TEXT, COL_IT_C_CAMARA TEXT, COL_IT_GARANTIA TEXT, " +
                "PRIMARY KEY(COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM))");
        db.execSQL("CREATE TABLE COLETA_COMISS_BORR (COLETA_COMISS_ID TEXT, COLETA_COMISS_FX_INI TEXT, COLETA_COMISS_FX_FIM TEXT, COLETA_COMISS_PERC_COMISS TEXT)");
        db.execSQL("CREATE TABLE COLETA_IDENTIFICADOR (COLETA_IDENTIFICADOR_SEQ INTEGER, COLETA_IDENTIFICADOR_VEND TEXT, COLETA_IDENTIFICADOR_NUM TEXT, COLETA_IDENTIFICADOR_DATA TEXT, PRIMARY KEY(COLETA_IDENTIFICADOR_VEND, COLETA_IDENTIFICADOR_NUM))");
        db.execSQL("CREATE TABLE CLIENTES_LOCAL (CLIENTES_LOCAL_COD TEXT, CLIENTES_LOCAL_LOJA TEXT, CLIENTES_LOCAL_CGC TEXT, CLIENTES_LOCAL_NOME TEXT, CLIENTES_LOCAL_INSCR TEXT, CLIENTES_LOCAL_EMAIL TEXT, " +
                "CLIENTES_LOCAL_END TEXT, CLIENTES_LOCAL_BAIRRO TEXT, CLIENTES_LOCAL_MUN TEXT, CLIENTES_LOCAL_CEP TEXT, CLIENTES_LOCAL_EST TEXT, CLIENTES_LOCAL_DDD TEXT, CLIENTES_LOCAL_TEL TEXT, CLIENTES_LOCAL_ENDCOB TEXT, " +
                "CLIENTES_LOCAL_BAIRROC TEXT, CLIENTES_LOCAL_MUNC TEXT, CLIENTES_LOCAL_ESTC TEXT, CLIENTES_LOCAL_CEPC TEXT, CLIENTES_LOCAL_DDDC TEXT, CLIENTES_LOCAL_TELC TEXT, CLIENTES_LOCAL_MSBLQL TEXT, " +
                "CLIENTES_LOCAL_COND TEXT, CLIENTES_LOCAL_ZCATCLI TEXT, CLIENTES_LOCAL_TABELA TEXT, CLIENTES_LOCAL_RECNO INTEGER, CLIENTES_LOCAL_CLI_GRP TEXT, CLIENTES_LOCAL_DESC_EXCL TEXT, " +
                "CLIENTES_LOCAL_BORR TEXT)");
        db.execSQL("CREATE TABLE BORRACHEIROS_LOCAL (BORRACH_LOCAL_COD TEXT PRIMARY KEY, BORRACH_LOCAL_NOME TEXT, BORRACH_LOCAL_DOC TEXT)");
        db.execSQL("CREATE TABLE PRODUTOS_LOCAL (PRODUTOS_LOCAL_CODIGO TEXT PRIMARY KEY, PRODUTOS_LOCAL_DESCR TEXT, PRODUTOS_LOCAL_BLQ TEXT, PRODUTOS_LOCAL_DESENHO TEXT, PRODUTOS_LOCAL_PRECO TEXT, PRODUTOS_LOCAL_TIPO_GRP TEXT, " +
                "PRODUTOS_LOCAL_GRUPO TEXT, PRODUTOS_LOCAL_DESC_VACHIOST TEXT, PRODUTOS_LOCAL_DESC_LAUXEN TEXT, PRODUTOS_LOCAL_ISDESC_GRP TEXT)");
        db.execSQL("CREATE TABLE TAB_PRECO_LOCAL (TAB_PRECO_LOCAL_ITEM TEXT, TAB_PRECO_LOCAL_COD_TAB TEXT, TAB_PRECO_LOCAL_COD_PRD TEXT, TAB_PRECO_LOCAL_PRECO TEXT)");
        db.execSQL("CREATE TABLE COND_PAG_LOCAL (COND_PAG_LOCAL_COD TEXT, COND_PAG_LOCAL_DESC TEXT, COND_PAG_LOCAL_FORMA TEXT, COND_PAG_LOCAL_DESC_FORMA TEXT, COND_PAG_LOCAL_TIPO TEXT, " +
                "COND_PAG_LOCAL_COND TEXT, COND_PAG_LOCAL_USO_EMPR TEXT, COND_PAG_LOCAL_USO_COLETA TEXT, COND_PAG_LOCAL_USO_FILIAL TEXT)");
        db.execSQL("CREATE TABLE PARAMETROS (PAR_FIL TEXT, PAR_VAR TEXT, PAR_DESCR TEXT, PAR_CONTEUDO TEXT)");
    }

    @SuppressWarnings("DanglingJavadoc")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /** Utilizado para atualizar o banco de dados sem necessidade de exclusão, if na VERSAO - 1 **/

//        if (db.getVersion() == 1) {
//            db.execSQL(" ALTER TABLE USUARIOS_VENDEDORES ADD COLUMN USRVEND_CLIENTES_PERMITIDOS TEXT;");
//            db.execSQL(" ALTER TABLE COND_PAG_LOCAL ADD COLUMN COND_PAG_LOCAL_USO_EMPR TEXT;");
//            db.execSQL(" ALTER TABLE COND_PAG_LOCAL ADD COLUMN COND_PAG_LOCAL_USO_COLETA TEXT;");
//            db.execSQL(" ALTER TABLE COND_PAG_LOCAL ADD COLUMN COND_PAG_LOCAL_USO_FILIAL TEXT;");
//        }
//        if (db.getVersion() == 2) {
//            db.execSQL(" ALTER TABLE USUARIOS_VENDEDORES ADD COLUMN USRVEND_COD_BORRACHEIRO TEXT;");
//        }
    }

    /**
     * DELEÇÃO DO BD SQLITE
     **/
    public boolean onDelete(Context context) {
        boolean deleted = false;
        try {
            deleted = context.deleteDatabase(NOME_BANCO);
        } catch (Exception expt) {
            expt.printStackTrace();
        }
        return deleted;
    }

    /**
     * INSERÇÕES
     **/
    public boolean insereUsuario(String id_usr, String nome_usr, String login_usr, String passwd_usr, String status_usr, String email_usr,
                                 String cpf_usr, String tipo_usr, String cod_tovs, String nome_totvs) {

        SQLiteDatabase db = this.getWritableDatabase();

        query = "INSERT INTO USUARIOS (ID_USR, NOME_USR, LOGIN_USR, PASSWD_USR, STATUS_USR, EMAIL_USR, CPF_USR, TIPO_USR, COD_TOTVS, NOME_TOTVS) VALUES (?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            CriptografaString criptografaString = new CriptografaString();

            dbStmt.clearBindings();
            dbStmt.bindString(1, id_usr);
            dbStmt.bindString(2, nome_usr);
            dbStmt.bindString(3, login_usr);
            dbStmt.bindString(4, criptografaString.encrypt(passwd_usr));
            dbStmt.bindString(5, (status_usr.equals("") ? "A" : status_usr));
            dbStmt.bindString(6, email_usr);
            dbStmt.bindString(7, cpf_usr);
            dbStmt.bindString(8, (tipo_usr.equals("") ? "V" : tipo_usr));
            dbStmt.bindString(9, cod_tovs);
            dbStmt.bindString(10, nome_totvs);
            dbStmt.execute();

            db.setTransactionSuccessful();
            resultado = true;

        } catch (Exception e) {
            GettersSetters.setExpSqLite(e.getMessage());
            e.printStackTrace();
            resultado = false;
        }

        db.endTransaction();

        return resultado;
    }

//    public boolean insereMunicipioEstado(String sigla_est, String desc_mun) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        query = "INSERT INTO ESTADOS_MUNICIPIOS (SIGLA_EST, DESC_MUN) VALUES (?,?);";
//        SQLiteStatement dbStmt = db.compileStatement(query);
//
//        db.beginTransaction();
//        try {
//            dbStmt.clearBindings();
//            dbStmt.bindString(1, sigla_est);
//            dbStmt.bindString(2, desc_mun);
//            dbStmt.execute();
//
//            db.setTransactionSuccessful();
//            resultado = true;
//        } catch (Exception e) {
//            GettersSetters.setExpSqLite(e.getMessage());
//            e.printStackTrace();
//            resultado = false;
//        }
//
//        db.endTransaction();
//        
//
//        return resultado;
//    }

    public boolean insereFilial(String id_emp, String id_filial, String cd_auxiliar, String rz_social, String nome_fant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valoresFil = new ContentValues();

        valoresFil.put(ID_EMP, id_emp);
        valoresFil.put(ID_FILIAL, id_filial);
        valoresFil.put(CD_AUXILIAR, cd_auxiliar);
        valoresFil.put(RZ_SOCIAL, rz_social);
        valoresFil.put(NOME_FANT, nome_fant);

        long result = db.insert(FILIAIS, null, valoresFil);

        return result != -1;
    }

    public boolean insereFiliaisUsuarios(String empresaid, String filialid, String cdauxiliar, String usuarioid, String rzsocial) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues valoresUsrFil = new ContentValues();
//
//        valoresUsrFil.put(EMPRESAID, empresaid);
//        valoresUsrFil.put(FILIALID, filialid);
//        valoresUsrFil.put(CDAUXILIAR, cdauxiliar);
//        valoresUsrFil.put(USUARIOID, usuarioid);
//        valoresUsrFil.put(RZSOCIAL, rzsocial);
//
//        long result = db.insert(FILIAIS_USUARIOS, null, valoresUsrFil);
////        

        SQLiteDatabase db = this.getWritableDatabase();

        query = "INSERT INTO FILIAIS_USUARIOS (EMPRESAID, FILIALID, CDAUXILIAR, USUARIOID, RZSOCIAL) VALUES (?,?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            dbStmt.clearBindings();
            dbStmt.bindString(1, empresaid);
            dbStmt.bindString(2, filialid);
            dbStmt.bindString(3, cdauxiliar);
            dbStmt.bindString(4, usuarioid);
            dbStmt.bindString(5, rzsocial);
            dbStmt.execute();

            db.setTransactionSuccessful();

            resultado = true;
        } catch (Exception e) {
            GettersSetters.setExpSqLite(e.getMessage());
            e.printStackTrace();
            resultado = false;
        }
        db.endTransaction();


        return resultado;
    }

    public boolean insereEstados(String sigla) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valoresEstados = new ContentValues();

        valoresEstados.put("SIGLA", sigla);

        long result = db.insert("ESTADOS", null, valoresEstados);

        return result != -1;
    }

    public boolean insereDadosColetaCabecalho(String col_cabec_filial, String col_cabec_identif, String col_cabec_data, String col_cabec_cod_cli,
                                              String col_cabec_loja_cli, String col_cabec_rz_social, String col_cabec_vendedor, String col_cabec_qnt_itens,
                                              String col_cabec_total, String col_cabec_cod_cond_pg, String col_cabec_cond_pg, String col_cabec_forma_pg, String col_cabec_info_adic,
                                              String col_cabec_borracheiro, String col_cabec_nome_borracheiro, String col_cabec_doc_borracheiro,
                                              byte[] col_cabec_assin_borr, byte[] col_cabec_assin_cli, String col_cabec_status_envio, String coL_cabec_seq_identificador,
                                              String col_cabec_tipo_coleta, String col_cabec_hora_coleta, String col_cabec_colet_por, String col_cabec_pdf, String col_cabec_id_usr,
                                              String col_cabec_doc_cli, String col_cabec_dt_chegada, String col_cabec_orcamento, String col_cabec_offline, String col_cabec_cli_novo) {

        SQLiteDatabase db = this.getWritableDatabase();

        query = "INSERT INTO COLETA_CABEC (COL_CABEC_FILIAL, COL_CABEC_IDENTIF, COL_CABEC_DATA, COL_CABEC_COD_CLI, " +
                "                                  COL_CABEC_LOJA_CLI, COL_CABEC_RZ_SOCIAL, COL_CABEC_VENDEDOR, COL_CABEC_QNT_ITENS, COL_CABEC_TOTAL, " +
                "                                  COL_CABEC_COD_COND_PG, COL_CABEC_COND_PG, COL_CABEC_FORMA_PG, COL_CABEC_INFO_ADIC, COL_CABEC_BORRACHEIRO, " +
                "                                  COL_CABEC_NOME_BORRACHEIRO, COL_CABEC_DOC_BORRACHEIRO, COL_CABEC_ASSIN_BORR, " +
                "                                  COL_CABEC_ASSIN_CLI, COL_CABEC_STATUS_ENVIO, COL_CABEC_SEQ_IDENTIFICADOR, COL_CABEC_TIPO_COLETA," +
                "                                  COL_CABEC_HORA_COLETA, COL_CABEC_COL_POR, COL_CABEC_PDF, COL_CABEC_ID_USR, COL_CABEC_DOC_CLI, COL_CABEC_DT_CHEGADA, " +
                "                                  COL_CABEC_ORCAMENTO, COL_CABEC_OFFLINE, COL_CABEC_CLI_NOVO) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            dbStmt.clearBindings();
            dbStmt.bindString(1, col_cabec_filial);
            dbStmt.bindString(2, col_cabec_identif);
            dbStmt.bindString(3, col_cabec_data);
            dbStmt.bindString(4, col_cabec_cod_cli);
            dbStmt.bindString(5, col_cabec_loja_cli);
            dbStmt.bindString(6, col_cabec_rz_social);
            dbStmt.bindString(7, col_cabec_vendedor);
            dbStmt.bindString(8, col_cabec_qnt_itens);
            dbStmt.bindString(9, col_cabec_total);
            dbStmt.bindString(10, col_cabec_cod_cond_pg);
            dbStmt.bindString(11, col_cabec_cond_pg);
            dbStmt.bindString(12, col_cabec_forma_pg);
            dbStmt.bindString(13, col_cabec_info_adic);
            dbStmt.bindString(14, col_cabec_borracheiro);
            dbStmt.bindString(15, col_cabec_nome_borracheiro);
            dbStmt.bindString(16, col_cabec_doc_borracheiro);
            dbStmt.bindBlob(17, col_cabec_assin_borr);
            dbStmt.bindBlob(18, col_cabec_assin_cli);
            dbStmt.bindString(19, col_cabec_status_envio);
            dbStmt.bindString(20, coL_cabec_seq_identificador);
            dbStmt.bindString(21, col_cabec_tipo_coleta);
            dbStmt.bindString(22, col_cabec_hora_coleta);
            dbStmt.bindString(23, col_cabec_colet_por);
            dbStmt.bindString(24, col_cabec_pdf);
            dbStmt.bindString(25, col_cabec_id_usr);
            dbStmt.bindString(26, col_cabec_doc_cli);
            dbStmt.bindString(27, col_cabec_dt_chegada);
            dbStmt.bindString(28, col_cabec_orcamento);
            dbStmt.bindString(29, col_cabec_offline);
            dbStmt.bindString(30, col_cabec_cli_novo);
            dbStmt.execute();

            db.setTransactionSuccessful();

            resultado = true;
        } catch (SQLException exp) {
            exp.printStackTrace();
            GettersSetters.setErroSalvarColetaBDInt(exp.getMessage());
            Log.v("Coleta Insere Cabec INT", "", exp.fillInStackTrace());
            resultado = false;
        }
        db.endTransaction();

        return resultado;
    }

    public boolean insereDadosColetaCli(String col_cli_filial, String col_cli_identif, String col_cli_data, String col_cli_cod_cli, String col_cli_loja_cli,
                                        String col_cli_doc_cli, String col_cli_ie, String col_cli_email, String col_cli_ent_end, String col_cli_ent_bairro,
                                        String col_cli_ent_munic, String col_cli_ent_est, String col_cli_ent_cep,
                                        String col_cli_ent_ddd, String col_cli_ent_fone, String col_cli_cobr_end,
                                        String col_cli_cobr_bairro, String col_cli_cobr_munic, String col_cli_cobr_est, String col_cli_cobr_cep, String col_cli_cobr_ddd,
                                        String col_cli_cobr_fone, String col_cli_info_adic, byte[] col_cli_rg, byte[] col_cli_cpf, byte[] col_cli_compResid,
                                        String col_cli_nome, String col_cli_categoria) {

        SQLiteDatabase db = this.getWritableDatabase();

        query = "INSERT INTO COLETA_CLI (COL_CLI_FILIAL, COL_CLI_IDENTIF, COL_CLI_DATA, COL_CLI_COD_CLI, COL_CLI_LOJA_CLI, COL_CLI_DOC_CLI, COL_CLI_IE, " +
                "                                COL_CLI_EMAIL, COL_CLI_ENT_END, COL_CLI_ENT_BAIRRO, COL_CLI_ENT_MUNIC, COL_CLI_ENT_EST, COL_CLI_ENT_CEP, COL_CLI_ENT_DDD, COL_CLI_ENT_FONE, " +
                "                                COL_CLI_COBR_END, COL_CLI_COBR_BAIRRO, COL_CLI_COBR_MUNIC, COL_CLI_COBR_EST, COL_CLI_COBR_CEP, COL_CLI_COBR_DDD, COL_CLI_COBR_FONE, " +
                "                                COL_CLI_INFO_ADIC, COL_CLI_RG, COL_CLI_CPF, COL_CLI_COMP_RESID, COL_CLI_NOME, COL_CLI_CATEGORIA) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            dbStmt.clearBindings();
            dbStmt.bindString(1, col_cli_filial);
            dbStmt.bindString(2, col_cli_identif);
            dbStmt.bindString(3, col_cli_data);
            dbStmt.bindString(4, col_cli_cod_cli);
            dbStmt.bindString(5, col_cli_loja_cli);
            dbStmt.bindString(6, col_cli_doc_cli);
            dbStmt.bindString(7, col_cli_ie);
            dbStmt.bindString(8, col_cli_email);
            dbStmt.bindString(9, col_cli_ent_end);
            dbStmt.bindString(10, col_cli_ent_bairro);
            dbStmt.bindString(11, col_cli_ent_munic);
            dbStmt.bindString(12, col_cli_ent_est);
            dbStmt.bindString(13, col_cli_ent_cep);
            dbStmt.bindString(14, col_cli_ent_ddd);
            dbStmt.bindString(15, col_cli_ent_fone);
            dbStmt.bindString(16, col_cli_cobr_end);
            dbStmt.bindString(17, col_cli_cobr_bairro);
            dbStmt.bindString(18, col_cli_cobr_munic);
            dbStmt.bindString(19, col_cli_cobr_est);
            dbStmt.bindString(20, col_cli_cobr_cep);
            dbStmt.bindString(21, col_cli_cobr_ddd);
            dbStmt.bindString(22, col_cli_cobr_fone);
            dbStmt.bindString(23, col_cli_info_adic);
            dbStmt.bindBlob(24, col_cli_rg);
            dbStmt.bindBlob(25, col_cli_cpf);
            dbStmt.bindBlob(26, col_cli_compResid);
            dbStmt.bindString(27, col_cli_nome);
            dbStmt.bindString(28, col_cli_categoria);
            dbStmt.execute();

            db.setTransactionSuccessful();

            resultado = true;
        } catch (SQLException exp) {
            exp.printStackTrace();
            GettersSetters.setErroSalvarColetaBDInt(exp.getMessage());
            Log.v("Coleta Insere Cli INT", "", exp.fillInStackTrace());
            resultado = false;
        }

        db.endTransaction();


        return resultado;
    }

    public boolean insereDadosColetaItens(String col_it_filial, String col_it_identif, String col_it_item, String col_it_data, String col_it_cod_cli,
                                          String col_it_loja_cli, String col_it_qtd, String col_it_valor_unit, String col_it_valor, String col_it_cod_prod,
                                          String col_it_bitola, String col_it_marca, String col_it_modelo, String col_it_serie, String col_it_dot, String col_it_montado,
                                          String col_it_desenho, String col_it_urgente, String col_it_perc_com_borr, String col_it_vlr_com_borr,
                                          String col_it_status_envio, String col_it_obs, String col_it_c_agua, String col_it_c_camara, String col_it_garantia) {

        SQLiteDatabase db = this.getWritableDatabase();

        query = "INSERT INTO COLETA_ITENS (COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM, COL_IT_DATA, COL_IT_COD_CLI, COL_IT_LOJA_CLI, COL_IT_QTD, " +
                "                                  COL_IT_VALOR_UNIT, COL_IT_VALOR, COL_IT_COD_PROD, COL_IT_BITOLA, COL_IT_MARCA, COL_IT_MODELO, COL_IT_SERIE, " +
                "                                  COL_IT_DOT, COL_IT_MONTADO, COL_IT_DESENHO, COL_IT_URGENTE, COL_IT_PERC_COM_BORR, COL_IT_VLR_COM_BORR, COL_IT_STATUS_ENVIO, " +
                "                                  COL_IT_OBS, COL_IT_C_AGUA, COL_IT_C_CAMARA, COL_IT_GARANTIA) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            dbStmt.clearBindings();
            dbStmt.bindString(1, col_it_filial);
            dbStmt.bindString(2, col_it_identif);
            dbStmt.bindString(3, col_it_item);
            dbStmt.bindString(4, col_it_data);
            dbStmt.bindString(5, col_it_cod_cli);
            dbStmt.bindString(6, col_it_loja_cli);
            dbStmt.bindString(7, col_it_qtd);
            dbStmt.bindString(8, col_it_valor_unit);
            dbStmt.bindString(9, col_it_valor);
            dbStmt.bindString(10, col_it_cod_prod);
            dbStmt.bindString(11, col_it_bitola);
            dbStmt.bindString(12, col_it_marca);
            dbStmt.bindString(13, col_it_modelo);
            dbStmt.bindString(14, col_it_serie);
            dbStmt.bindString(15, col_it_dot);
            dbStmt.bindString(16, col_it_montado);
            dbStmt.bindString(17, col_it_desenho);
            dbStmt.bindString(18, col_it_urgente);
            dbStmt.bindString(19, col_it_perc_com_borr);
            dbStmt.bindString(20, col_it_vlr_com_borr);
            dbStmt.bindString(21, col_it_status_envio);
            dbStmt.bindString(22, col_it_obs);
            dbStmt.bindString(23, col_it_c_agua);
            dbStmt.bindString(24, col_it_c_camara);
            dbStmt.bindString(25, col_it_garantia);

            dbStmt.execute();

            db.setTransactionSuccessful();

            resultado = true;
        } catch (SQLException exp) {
            exp.printStackTrace();
            GettersSetters.setErroSalvarColetaBDInt(exp.getMessage());
            Log.v("Coleta Insere Itens INT", "", exp.fillInStackTrace());
            resultado = false;
        }

        db.endTransaction();


        return resultado;
    }

    public void insereNumColeta(String sequencial, String codVend, String numeroColeta, String data) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("COLETA_IDENTIFICADOR", null, null);

        query = "INSERT INTO COLETA_IDENTIFICADOR (COLETA_IDENTIFICADOR_SEQ, COLETA_IDENTIFICADOR_VEND, COLETA_IDENTIFICADOR_NUM, COLETA_IDENTIFICADOR_DATA) VALUES (?,?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(query);

        db.beginTransaction();
        try {
            dbStmt.clearBindings();
            dbStmt.bindString(1, sequencial);
            dbStmt.bindString(2, codVend);
            dbStmt.bindString(3, numeroColeta);
            dbStmt.bindString(4, data);
            dbStmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        db.endTransaction();

    }

    /**
     * BUSCAS*
     */
    public Cursor selectUsuariosVendedores(String idUsuario, String tipoUsuario, boolean impressao, boolean isLogin) {
        SQLiteDatabase db = this.getWritableDatabase();

        arrIdUsuario.clear();
        arrUsuario.clear();
        arrCodTotvs.clear();
        arrNomeTotvs.clear();
        arrTipovend.clear();
        arrCliPermitidos.clear();
        arrCodBorrachPat.clear();

        buscaColetaNomeVend = "";

        query = "SELECT " +
                "       ifnull(USRVEND_ID,'') ID, " +
                "       ifnull(upper(USRVEND_NOME),'') NOME, " +
                "       ifnull(USRVEND_COD_TOTVS,'') COD_TOTVS, " +
                "       ifnull(USRVEND_NOME_TOTVS,'') NOME_TOTVS, " +
                "       ifnull(USRVEND_TIPO_VEND,'') TIPO, " +
                "       ifnull(USRVEND_CLIENTES_PERMITIDOS,'') CLI_PERMT, " + //5
                "       ifnull(USRVEND_COD_BORRACHEIRO,'') COD_BORR " +
                "FROM USUARIOS_VENDEDORES " +
                "WHERE 1 = 1 ";
        if (isLogin) { //NO LOGIN BUSCA MENOS DADOS PARA AGILIZAR A INICIALIZAÇÃO
            query += " AND USRVEND_TIPO_VEND IN ('V') ";
        } else {
            if (impressao) {
                query += "AND trim(USRVEND_COD_TOTVS) = '" + idUsuario.trim() + "' ";
            } else {
                if (tipoUsuario.trim().equals("G")) {
                    query += "AND trim(USRVEND_GERENTE) = '" + idUsuario.trim() + "' ";
                } else if (tipoUsuario.trim().equals("P") || tipoUsuario.trim().equals("T")) {
                    query += "AND trim(USRVEND_ID_PERMITIDO) LIKE ('%" + idUsuario.trim() + "%')";
                } else {
                    query += " AND trim(USRVEND_COD_TOTVS) <> '' " +
                            " AND USRVEND_TIPO_VEND IN ('V','T','P') ";
                }
            }
        }
        query += "ORDER BY upper(USRVEND_NOME)";

//        System.out.println(query);

        try (Cursor res = db.rawQuery(query, null)) {
            if (res.moveToFirst()) {
                do {
                    if (!impressao) {
                        arrIdUsuario.add(res.getString(0).trim());
                        arrUsuario.add(res.getString(1).trim());
                        arrCodTotvs.add(res.getString(2).trim());
                        arrNomeTotvs.add(res.getString(3).trim());
                        arrTipovend.add(res.getString(4).trim());
                        arrCliPermitidos.add(res.getString(5).trim());
                        arrCodBorrachPat.add(res.getString(6).trim());
                    } else {
                        buscaColetaNomeVend = (res.getString(1).trim());
                    }
                } while (res.moveToNext());
            }
            res.close();
            return res;
        }
    }

    public Cursor selecionaComissaoBorracheiro() {
        SQLiteDatabase db = this.getWritableDatabase();

        arrIdComissBorr.clear();
        arrFxIniComissBorr.clear();
        arrFxFimComissBorr.clear();
        arrPercComissBorr.clear();

        query = "SELECT * FROM  COLETA_COMISS_BORR";

        try (Cursor res = db.rawQuery(query, null)) {
            if (res.moveToFirst()) {
                do {
                    arrIdComissBorr.add(res.getString(0));
                    arrFxIniComissBorr.add(res.getString(1));
                    arrFxFimComissBorr.add(res.getString(2));
                    arrPercComissBorr.add(res.getDouble(3));
                } while (res.moveToNext());
            }

            return res;
        }
    }

    public Cursor buscaColetasNaoEnviadas(String numero, String filial, boolean envia, String vendedor) {
        SQLiteDatabase db = this.getReadableDatabase();

        query = "SELECT " +
                "        COL_CABEC_FILIAL, " +
                "        COL_CABEC_IDENTIF, " +
                "        COL_CABEC_DATA, " +
                "        COL_CABEC_COD_CLI, " +
                "        COL_CABEC_LOJA_CLI, " +
                "        COL_CABEC_RZ_SOCIAL, " +
                "        COL_CABEC_VENDEDOR, " +
                "        COL_CABEC_ID_USR, " +
                "        COL_CABEC_COL_POR " +
                " FROM COLETA_CABEC CC " +
                " LEFT JOIN USUARIOS_VENDEDORES UV ON UV.USRVEND_ID = CC.COL_CABEC_ID_USR " +
                " WHERE 1 = 1 " +
                "  AND trim(COL_CABEC_STATUS_ENVIO) = '1' " +
                "  AND trim(COL_CABEC_FILIAL) <> '' " +
                "   AND trim(COL_CABEC_IDENTIF) <> '' ";
        if (!vendedor.equals("") && !GettersSetters.getTipoUsuario().equals("P")) { //PARA QUANDO VENDEDOR (V) OU TERCEIRO (T) LOGADO, SOMENTE APARECERÃO AS SUAS COLETAS.
            query += " AND (trim(COL_CABEC_VENDEDOR) = '" + vendedor.trim() + "' OR trim(COL_CABEC_COL_POR) = '"+ vendedor.trim() +"')";
        } else if (GettersSetters.getTipoUsuario().equals("P")) {
            query += " AND (trim(UV.USRVEND_ID_PERMITIDO) LIKE '%" + vendedor.trim() + "%' OR COL_CABEC_VENDEDOR = '"+ vendedor.trim() +"')";
        }
        if (!numero.trim().equals("")) {
            query += " AND trim(COL_CABEC_FILIAL) = '" + filial.trim() + "'";
            query += " AND trim(COL_CABEC_IDENTIF) = '" + numero.trim() + "'";
        }
        query += " ORDER BY COL_CABEC_IDENTIF ";

//        System.out.println(query);

        if (envia) {
            arrBuscaColetaNaoEnviadaFilial.clear();
            arrBuscaColetaNaoEnviada.clear();
            arrBuscaColetaNaoEnviadaData.clear();
            arrBuscaColetaNaoEnviadaNomeCli.clear();
            arrBuscaColetaNaoEnviadaCodCli.clear();
            arrBuscaColetaNaoEnviadaLojaCli.clear();
            arrBuscaColetaNaoEnviadaCodVend.clear();
            arrBuscaColetaNaoEnviadaColPor.clear();
            arrBuscaColetaNaoEnviadaCodCRecap.clear();

            try (Cursor res = db.rawQuery(query, null)) {
                if (res.moveToFirst()) {
                    do {
                        arrBuscaColetaNaoEnviadaFilial.add(res.getString(0));
                        arrBuscaColetaNaoEnviada.add(res.getString(1));
                        arrBuscaColetaNaoEnviadaData.add(res.getString(2));
                        arrBuscaColetaNaoEnviadaCodCli.add(res.getString(3));
                        arrBuscaColetaNaoEnviadaLojaCli.add(res.getString(4));
                        arrBuscaColetaNaoEnviadaNomeCli.add(res.getString(5));
                        arrBuscaColetaNaoEnviadaCodVend.add(res.getString(6));
                        arrBuscaColetaNaoEnviadaCodCRecap.add(res.getString(7));
                        arrBuscaColetaNaoEnviadaColPor.add(res.getString(8));
                    } while (res.moveToNext());
                }
            }
        }
        return db.rawQuery(query, null);
    }

    public Cursor buscaEstados() {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("SELECT * FROM ESTADOS", null)) {

            if (res.moveToFirst()) {
                do {
                    String siglaEst = res.getString(0);
                    arrEstados.add(siglaEst);
                } while (res.moveToNext());
            }


            return res;
        }
    }

//    public Cursor buscaMunicipios(String estado) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        arrMunEst.clear();
//
//        try (Cursor res = db.rawQuery("SELECT DISTINCT DESC_MUN FROM ESTADOS_MUNICIPIOS WHERE SIGLA_EST = ?", new String[]{estado})) {
//            if (res.moveToFirst()) {
//                do {
//                    arrMunEst.add(res.getString(0));
//                } while (res.moveToNext());
//            }
//            return res;
//        }
//    }

    public Cursor buscaUsuarios(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();

        query = "SELECT ifnull(ID_USR,''), " +
                "       ifnull(NOME_USR,''), " +
                "       ifnull(LOGIN_USR,''), " +
                "       ifnull(PASSWD_USR,''), " +
                "       ifnull(STATUS_USR,''), " +
                "       ifnull(EMAIL_USR,''), " +
                "       ifnull(CPF_USR,''), " +
                "       ifnull(TIPO_USR,''), " +
                "       ifnull(COD_TOTVS,''), " +
                "       ifnull(NOME_TOTVS,'') " +
                "FROM USUARIOS " +
                "WHERE 1 = 1" +
                "  AND trim(LOGIN_USR) = '" + usuario.trim() + "'";

        return db.rawQuery(query, null);
    }

    public Cursor buscaFilais() {
        SQLiteDatabase db = this.getReadableDatabase();
//        arrDadosFilial.clear();
//        arrDadosFiliaisUsuarios.clear();
//        try (Cursor res = db.rawQuery("SELECT * FROM " + FILIAIS + " ORDER BY CD_AUXILIAR", null)) {
//            if (res.moveToFirst()) {
//                do {
//                    if (GettersSetters.getCodigoVendedor().equals("000")) {
//                        arrDadosFiliaisUsuarios.add(res.getString(3) + " / " + res.getString(2));
//                    } else {
//                        arrDadosFilial.add(res.getString(4)); //razão social
//                    }
//                } while (res.moveToNext());
//            }
        return db.rawQuery("SELECT * FROM " + FILIAIS + " ORDER BY CD_AUXILIAR", null);
    }
//    }

//    public Cursor buscaFiliaisUsuarios() { //seleciona todas as filiais x usuários
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        arrDadosFiliaisUsuarios.clear();
//        arrDescrFilialUsuario.clear();
//        arrCodFilialUsuario.clear();
//
//        try (Cursor res = db.rawQuery("SELECT * FROM " + FILIAIS_USUARIOS, null)) {
//            if (res.moveToFirst()) {
//                do {
//                    arrDadosFiliaisUsuarios.add(res.getString(4) + " / " + res.getString(2)); //razão social + codigo auxiliar
//                    arrCodFilialUsuario.add(res.getString(2));
//                    arrDescrFilialUsuario.add(res.getString(4));
//                } while (res.moveToNext());
//            }
//            return res;
//        }
//    }

    public Cursor buscaFiliaisUsuarios(String idUsuario) { //seleciona todas as filiais x usuários
        SQLiteDatabase db = this.getReadableDatabase();

        arrDadosFiliaisUsuarios.clear();
        arrCodFilialUsuario.clear();
        arrDescrFilialUsuario.clear();

        query = "SELECT DISTINCT CDAUXILIAR, " +
                "                USUARIOID, " +
                "                RZSOCIAL " +
                "FROM FILIAIS_USUARIOS " +
                " WHERE 1 = 1";
        if (!idUsuario.trim().equals("")) {
            query += " AND trim(USUARIOID) = '" + idUsuario.trim() + "'";
        }

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    arrDadosFiliaisUsuarios.add(res.getString(2) + " / " + res.getString(0));
                    arrCodFilialUsuario.add(res.getString(0));
                    arrDescrFilialUsuario.add(res.getString(2));
                } while (res.moveToNext());
            }
            return res;
        }
    }

    public String buscaFiliaisDescr(String filial) {
        descrFilial = "";

        query = "SELECT * " +
                "FROM FILIAIS " +
                " WHERE trim(CD_AUXILIAR) = '" + filial.trim() + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    descrFilial = res.getString(3).trim();
                } while (res.moveToNext());
            }
            return descrFilial;
        }
    }

    public Cursor buscaCondPgto(String formaPgto, String condPgtoCli) {
        SQLiteDatabase db = this.getReadableDatabase();

        arrcodCondPgto.clear();
        arrdescCondPgto.clear();
        arrformaCondPgto.clear();
        arrdescFormaCondPgto.clear();
        arrCondPgto.clear();

        query = "SELECT DISTINCT " +
                "                   COND_PAG_LOCAL_COD, " +
                "                   COND_PAG_LOCAL_DESC, " +
                "                   COND_PAG_LOCAL_FORMA, " +
                "                   COND_PAG_LOCAL_DESC_FORMA, " +
                "                   COND_PAG_LOCAL_TIPO, " +
                "                   COND_PAG_LOCAL_COND " +
                "             FROM COND_PAG_LOCAL " +
                "             WHERE 1 = 1 ";
        if (condPgtoCli.equals("")) {
            query += "AND COND_PAG_LOCAL_COD NOT IN ('001','P31','P20','P21','D00','D40','001') ";
            if (GettersSetters.getFilial().startsWith("1101") || GettersSetters.getFilial().startsWith("1201")) {
                query += "AND (trim(COND_PAG_LOCAL_USO_EMPR) IN ('2','9') OR COND_PAG_LOCAL_USO_FILIAL LIKE '%" + GettersSetters.getFilial() + "%')";
            } else if (GettersSetters.getFilial().startsWith("1301") || GettersSetters.getFilial().startsWith("1401")) {
                query += "AND (trim(COND_PAG_LOCAL_USO_EMPR) IN ('3','9') OR COND_PAG_LOCAL_USO_FILIAL LIKE '%" + GettersSetters.getFilial() + "%')";
            } else {
                query += "AND (trim(COND_PAG_LOCAL_USO_EMPR) IN ('1','9','') OR COND_PAG_LOCAL_USO_FILIAL LIKE '%" + GettersSetters.getFilial() + "%')";
            }
        } else {
            query += "AND COND_PAG_LOCAL_COD = '" + condPgtoCli + "' ";
        }

        if (formaPgto.equals("")) {
            query += "AND COND_PAG_LOCAL_FORMA <> '' ";
        } else {
            query += "AND COND_PAG_LOCAL_FORMA LIKE '%" + formaPgto + "%' ";
        }

//        query += " ORDER BY COND_PAG_LOCAL_COND ";

//        System.out.println(query);

        try (Cursor res = db.rawQuery(query, null)) {
            if (res.moveToFirst()) {
                do {
                    arrcodCondPgto.add(res.getString(0));
                    arrdescCondPgto.add(res.getString(1));
                    arrformaCondPgto.add(res.getString(2));
                    arrdescFormaCondPgto.add(res.getString(3));
                    arrCondPgto.add(res.getString(5));

                    if (formaPgto.trim().equals("BOL") &&
                            res.getString(5).contains(",") &&
                            Double.parseDouble(GettersSetters.getValorColeta()) < 200 &&
                            (!GettersSetters.getFilial().startsWith("1101") && !GettersSetters.getFilial().startsWith("1201"))) {
                        arrcodCondPgto.remove(res.getString(0));
                        arrdescCondPgto.remove(res.getString(1));
                        arrformaCondPgto.remove(res.getString(2));
                        arrdescFormaCondPgto.remove(res.getString(3));
                        arrCondPgto.remove(res.getString(5));
                    }

                } while (res.moveToNext());
            }

            return res;
        }
    }

    public Cursor buscaCondPgColeta(String numeroColeta, String filial, String docCli) {
        SQLiteDatabase db = this.getReadableDatabase();

        query = "SELECT COL_CABEC_COND_PG, " +
                "       COL_CABEC_FORMA_PG " +
                " FROM COLETA_CABEC " +
                "WHERE 1 = 1 " +
                "  AND trim(COL_CABEC_IDENTIF) = '" + numeroColeta.trim() + "' " +
                "  AND trim(COL_CABEC_FILIAL) = '" + filial.trim() + "' " +
                "  AND trim(COL_CABEC_DOC_CLI) = '" + docCli.trim() + "' ";

        return db.rawQuery(query, null);
    }

    public Double selectTabPrcCli(String tabPrcCli, String codProd) {
        SQLiteDatabase db = this.getReadableDatabase();
        double valorTabPreco = 0;

        query = "SELECT TAB_PRECO_LOCAL_ITEM, TAB_PRECO_LOCAL_COD_TAB, TAB_PRECO_LOCAL_COD_PRD, ifnull(TAB_PRECO_LOCAL_PRECO,'0') " +
                "FROM TAB_PRECO_LOCAL " +
                "WHERE 1 = 1 " +
                "  AND trim(TAB_PRECO_LOCAL_COD_TAB) = '" + tabPrcCli.trim() + "' " +
                "  AND trim(TAB_PRECO_LOCAL_COD_PRD) = '" + codProd.trim() + "'";

        //System.out.println(query);

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    valorTabPreco = res.getDouble(3);
                } while (res.moveToNext());
            }
        }

        return valorTabPreco;
    }

    public Cursor buscaProduto(String descrProduto, boolean isPnAgricola, String codigoProduto) {
        SQLiteDatabase db = this.getReadableDatabase();

        query = "SELECT PRODUTOS_LOCAL_CODIGO, PRODUTOS_LOCAL_DESCR, PRODUTOS_LOCAL_DESENHO, " +
                "       PRODUTOS_LOCAL_PRECO, PRODUTOS_LOCAL_BLQ, PRODUTOS_LOCAL_DESC_VACHIOST, " +
                "       PRODUTOS_LOCAL_DESC_LAUXEN, PRODUTOS_LOCAL_ISDESC_GRP " +
                "FROM PRODUTOS_LOCAL " +
                "WHERE 1 = 1 ";
        if (!descrProduto.trim().equals("")) {
            query += "AND PRODUTOS_LOCAL_DESCR LIKE '%" + descrProduto.replace(" ", "%") + "%' ";
        }
        if (isPnAgricola) {
            query += "AND PRODUTOS_LOCAL_DESCR NOT LIKE '%RECAP%' ";
        }
        if (!GettersSetters.getTipoColeta().equals("")) {
            query += "AND PRODUTOS_LOCAL_TIPO_GRP IN (" + GettersSetters.getTipoColeta() + ") ";
        } else {
            query += "AND PRODUTOS_LOCAL_TIPO_GRP NOT IN ('SE','PC','SP','CA') ";
        }
        if (!codigoProduto.equals("")) {
            query += "AND PRODUTOS_LOCAL_CODIGO = '" + codigoProduto + "' ";
        }
        query += "AND PRODUTOS_LOCAL_TIPO_GRP NOT IN ('CO','MP','IN','OU') " +
                "GROUP BY PRODUTOS_LOCAL_CODIGO, PRODUTOS_LOCAL_DESCR, PRODUTOS_LOCAL_DESENHO, " +
                "         PRODUTOS_LOCAL_PRECO, PRODUTOS_LOCAL_BLQ, PRODUTOS_LOCAL_DESC_VACHIOST, " +
                "         PRODUTOS_LOCAL_DESC_LAUXEN, PRODUTOS_LOCAL_ISDESC_GRP " +
                "ORDER BY 2";

        //System.out.println(qryProdutos);

        arrCodProdutos.clear();
        arrDescProdutos.clear();
        arrProdBlq.clear();
        arrDesenhoProd.clear();
        arrPrecoTabela.clear();
        arrDescVachiOst.clear();
        arrDescLauxen.clear();
        arrIsDescGrp.clear();

        codProd = "";
        descrProd = "";
        prdBlq = "";
        descDesenho = "";
        precoTabela = "";
        descontVachiOst = "";
        descontLauxen = "";

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    codProd = res.getString(0);
                    descrProd = res.getString(1);
                    descDesenho = res.getString(2);
                    precoTabela = res.getString(3);
                    prdBlq = res.getString(4);
                    descontVachiOst = res.getString(5);
                    descontLauxen = res.getString(6);

                    if (prdBlq.equals("1")) {
                        descrProd = "*** " + descrProd;
                    }

                    if (precoTabela.trim().isEmpty()) {
                        precoTabela = "0";
                    }

                    arrCodProdutos.add(codProd);
                    arrDescProdutos.add(descrProd);
                    arrDesenhoProd.add(descDesenho);
                    arrPrecoTabela.add(precoTabela);
                    arrProdBlq.add(prdBlq);
                    arrDescVachiOst.add(descontVachiOst);
                    arrDescLauxen.add(descontLauxen);
                    arrIsDescGrp.add(res.getString(7).trim().equals("T"));

                } while (res.moveToNext());
            }
            return res;
        }
    }

    public Cursor buscaCondPagLocal() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM COND_PAG_LOCAL", null);
    }

    public byte[] buscaAssinaturas(String campo, String codCli, String lojaCli, String identificador, String data) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] assinatura = new byte[0];

        String qryBuscaAssinatudas = "SELECT ifnull(" + campo.toUpperCase() + ",'') " +
                "FROM COLETA_CABEC " +
                "WHERE 1 = 1 " +
                "  AND trim(COL_CABEC_COD_CLI) = '" + codCli.trim() + "' " +
                "  AND trim(COL_CABEC_LOJA_CLI) = '" + lojaCli.trim() + "' " +
                "  AND trim(COL_CABEC_IDENTIF) = '" + identificador.trim() + "' " +
                "  AND trim(COL_CABEC_DATA) = '" + data.trim() + "'";

        //System.out.println(qryBuscaAssinatudas);

        try (Cursor rsAssinatura = db.rawQuery(qryBuscaAssinatudas, null)) {
            if (rsAssinatura != null && rsAssinatura.moveToFirst()) {
                do {
                    assinatura = rsAssinatura.getBlob(0);
                } while (rsAssinatura.moveToNext());
            }
            return assinatura;
        }
    }

    public Cursor buscaClientesColeta(String docCliente, String nomeCLiente, String recno, ArrayList<String> arrCliPerm, String codigo, String loja) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] clientesPerm;
        StringBuilder clientePermitidoBusca = new StringBuilder();
        FuncoesGenericas funcoesGenericas = new FuncoesGenericas();

        arrNomeCliente.clear();
        arrDocCliente.clear();
        arrMunCliente.clear();
        arrEstCliente.clear();
        arrIECliente.clear();
        arrRecnoCliente.clear();

        cgcCliente = "";
        nomeCli = "";
        ieCli = "";
        emailCli = "";
        endCli = "";
        bairroCli = "";
        munCli = "";
        cepCli = "";
        ufCli = "";
        dddCli = "";
        foneCli = "";
        endCobCli = "";
        bairroCobCli = "";
        munCobCli = "";
        ufCobCli = "";
        cepCobCli = "";
        telexCli = "";
        cliBlq = "";
        condPgtoCli = "";
        codCli = "";
        lojaCli = "";
        catCli = "";
        tabPrcCli = "";
        clienteGrp = false;
        descExclusivo = 0;
        codBorr = "";

        if (arrCliPerm.size() > 0) {
            clientesPerm = new String[arrCliPerm.size()];
            for (int i = 0; i < arrCliPerm.size(); i++) {
                clientesPerm[i] = arrCliPerm.get(i).replace(";", "");
            }
            clientesPerm = clientesPerm[0].split("/");

            for (int i = 0; i < clientesPerm.length; i++) {
                clientePermitidoBusca.append(funcoesGenericas.cripto(clientesPerm[i]));

                if (i < clientesPerm.length) {
                    clientePermitidoBusca.append("','");
                }
            }
        }

        query = "SELECT " +
                "         CLIENTES_LOCAL_CGC, \n " +
                "         TRIM(CLIENTES_LOCAL_NOME), \n " +
                "         TRIM(CLIENTES_LOCAL_INSCR), \n " +
                "         TRIM(CLIENTES_LOCAL_EMAIL), \n " +
                "         TRIM(CLIENTES_LOCAL_END), \n " +
                "         TRIM(CLIENTES_LOCAL_BAIRRO), \n " +
                "         TRIM(CLIENTES_LOCAL_MUN), \n " +
                "         CLIENTES_LOCAL_CEP, \n " +
                "         CLIENTES_LOCAL_EST, \n " +
                "         CLIENTES_LOCAL_DDD, \n " +
                "         CLIENTES_LOCAL_TEL, \n " +
                "         TRIM(CLIENTES_LOCAL_ENDCOB), \n " +
                "         TRIM(CLIENTES_LOCAL_BAIRROC), \n " +
                "         TRIM(CLIENTES_LOCAL_MUNC), \n " +
                "         CLIENTES_LOCAL_ESTC, \n " +
                "         CLIENTES_LOCAL_CEPC, \n " +
                "         CLIENTES_LOCAL_MSBLQL, \n " +
                "         CLIENTES_LOCAL_COND, \n " +
                "         CLIENTES_LOCAL_COD, \n " +
                "         CLIENTES_LOCAL_LOJA, \n " +
                "         CLIENTES_LOCAL_ZCATCLI, \n " +
                "         CLIENTES_LOCAL_TABELA, \n " +
                "         CLIENTES_LOCAL_DDDC, \n " +
                "         CLIENTES_LOCAL_TELC, \n " +
                "         CLIENTES_LOCAL_RECNO, \n " +
                "         CLIENTES_LOCAL_CLI_GRP, \n " +
                "         CLIENTES_LOCAL_DESC_EXCL, \n" +
                "         TRIM(CLIENTES_LOCAL_BORR) \n " +
                "   FROM CLIENTES_LOCAL \n " +
                "    WHERE 1 = 1 \n";

        if (codigo.trim().equals("")) {
            if (arrCliPerm.size() > 0) {
                query += "AND trim(CLIENTES_LOCAL_COD) || trim(CLIENTES_LOCAL_LOJA) IN ('" + clientePermitidoBusca + "') \n";
            }

            if (recno.trim().equals("")) {
                if (!docCliente.equals("") && nomeCLiente.equals("")) {
                    query += "AND trim(CLIENTES_LOCAL_CGC) = '" + funcoesGenericas.cripto(docCliente.trim()) + "' \n";
                } else if (docCliente.equals("")) {
                    query += "AND trim(CLIENTES_LOCAL_NOME) LIKE '%" + funcoesGenericas.cripto(nomeCLiente.trim()).replace(funcoesGenericas.descripto(" "), "%") + "%' \n";
                } else {
                    query += "AND trim(CLIENTES_LOCAL_CGC) = '" + funcoesGenericas.cripto(docCliente.trim()) + "' \n";
                    query += "AND trim(CLIENTES_LOCAL_NOME) = '" + funcoesGenericas.cripto(nomeCLiente.trim()) + "' \n";
                }
            } else {
                query += " AND trim(CLIENTES_LOCAL_RECNO) = '" + funcoesGenericas.cripto(recno.trim()) + "' \n";
            }
        } else {
            query += " AND trim(CLIENTES_LOCAL_COD) = '" + funcoesGenericas.cripto(codigo.trim()) + "' \n" +
                    "  AND trim(CLIENTES_LOCAL_LOJA) = '" + funcoesGenericas.cripto(loja.trim()) + "' \n";
        }
        query += " ORDER BY CLIENTES_LOCAL_NOME ASC";

//        System.out.println(query);

        Cursor rsSelecionaCliente = db.rawQuery(query, null);
        if (rsSelecionaCliente.moveToFirst()) {
            do {
                cgcCliente = funcoesGenericas.descripto(rsSelecionaCliente.getString(0));
                nomeCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(1));
                ieCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(2));
                emailCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(3));
                endCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(4));
                bairroCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(5));
                munCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(6));
                cepCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(7));
                ufCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(8));
                dddCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(9));
                foneCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(10));
                endCobCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(11));
                bairroCobCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(12));
                munCobCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(13));
                ufCobCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(14));
                cepCobCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(15));
                cliBlq = funcoesGenericas.descripto(rsSelecionaCliente.getString(16));
                condPgtoCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(17));
                codCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(18));
                lojaCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(19));
                catCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(20));
                tabPrcCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(21));
                ddd2Cli = funcoesGenericas.descripto(rsSelecionaCliente.getString(22));
                telexCli = funcoesGenericas.descripto(rsSelecionaCliente.getString(23));
                clienteGrp = funcoesGenericas.descripto(rsSelecionaCliente.getString(25)).trim().equals("S");
                descExclusivo = Double.parseDouble(funcoesGenericas.descripto(rsSelecionaCliente.getString(26)));
                codBorr = funcoesGenericas.descripto(rsSelecionaCliente.getString(27));

                if (cliBlq.equals("1")) {
                    arrNomeCliente.add("*** " + nomeCli);
                    nomeCli = "*** " + nomeCli;
                } else {
                    arrNomeCliente.add(nomeCli);
                }

                arrDocCliente.add(cgcCliente);
                arrMunCliente.add(munCli);
                arrEstCliente.add(ufCli);
                arrIECliente.add(ieCli);
                arrRecnoCliente.add(funcoesGenericas.descripto(rsSelecionaCliente.getString(24)));

                cliBlq = "";

            } while (rsSelecionaCliente.moveToNext());
        }

//            System.out.println(query);

        if (cliBlq != null) {
            GettersSetters.setClienteBlq(cliBlq);
        } else {
            GettersSetters.setClienteBlq("");
        }

        if (condPgtoCli != null) {
            GettersSetters.setCodCondPgto(condPgtoCli);
        } else {
            GettersSetters.setCodCondPgto("");
        }
        return rsSelecionaCliente;
    }

    public Cursor selectBorracheiro(String nomeBorracheiro, String docBorracheiro, String codBorr) {
        arrcodBorr.clear();
        arrnomeBorr.clear();
        arrdocBorr.clear();

        nomeBorr = "";
        docBorr = "";

        SQLiteDatabase db = this.getReadableDatabase();

        query = " SELECT BORRACH_LOCAL_COD, LTRIM(RTRIM(BORRACH_LOCAL_NOME)), LTRIM(RTRIM(BORRACH_LOCAL_DOC)) " +
                "FROM BORRACHEIROS_LOCAL " +
                "WHERE 1 = 1 ";
        if (!codBorr.equals("")) {
            query += "AND BORRACH_LOCAL_COD = '" + codBorr + "' ";
        } else {
            if (!nomeBorracheiro.equals("")) {
                query += "AND trim(BORRACH_LOCAL_NOME) LIKE '%" + nomeBorracheiro.trim() + "%' ";
            } else if (!docBorracheiro.equals("")) {
                query += "AND trim(BORRACH_LOCAL_DOC) = '" + docBorracheiro.trim() + "' ";
            }
        }
        query += "ORDER BY BORRACH_LOCAL_NOME";

        //System.out.println(qrySelectBorracheiro);

        try (Cursor rsSelecionaBorracheiro = db.rawQuery(query, null)) {
            if (rsSelecionaBorracheiro != null && rsSelecionaBorracheiro.moveToFirst()) {
                do {
                    try {
                        codBorr = rsSelecionaBorracheiro.getString(0);
                        nomeBorr = rsSelecionaBorracheiro.getString(1);
                        docBorr = rsSelecionaBorracheiro.getString(2);

                        arrcodBorr.add(rsSelecionaBorracheiro.getString(0));
                        arrnomeBorr.add(rsSelecionaBorracheiro.getString(1));
                        arrdocBorr.add(rsSelecionaBorracheiro.getString(2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (rsSelecionaBorracheiro.moveToNext());
            }
            return rsSelecionaBorracheiro;
        }
    }

    public Cursor buscaColetasCabec(String data, String numero, String nome, int status, String filial, int envio, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        arrBuscaColetaFilial.clear();
        arrBuscaNumeroColeta.clear();
        arrBuscaColetaData.clear();
        arrBuscaColetaCodCli.clear();
        arrBuscaColetaLojaCli.clear();
        arrBuscaColetaNomeCli.clear();
        arrBuscaColetaCodVend.clear();
        arrBuscaColetaQtdItens.clear();
        arrBuscaColetaValor.clear();
        arrBuscaColetaCondPg.clear();
        arrBuscaColetaCodCondPg.clear();
        arrBuscaColetaFormaPg.clear();
        arrBuscaColetaInfoAdic.clear();
        arrBuscaColetaCodBorr.clear();
        arrBuscaColetaNomeBorr.clear();
        arrBuscaColetaDocumBorr.clear();
        arrBuscaColetaAssinBorr.clear();
        arrBuscaColetaAssinCli.clear();
        arrBuscaColetaStatus.clear();
        arrBuscaColetaStatusEmail.clear();
        arrBuscaColetaIdSeq.clear();
        arrBuscaColetaTipoColeta.clear();
        arrBuscaColetaHoraColeta.clear();
        arrBuscaColetaColPor.clear();
        arrBuscaColetaPdf.clear();
        arrBuscaColetaIdUsr.clear();
        arrBuscaColetaDocCli.clear();
        arrBuscaColetaNomeVend.clear();
        arrBuscaColetaDtChegada.clear();
        arrBuscaColetaOrcamento.clear();
        arrBuscaColetaOffline.clear();
        arrBuscaColetaCliNovo.clear();

        buscaColetaFilial = "";
        buscaColetaNumero = "";
        buscaColetaData = "";
        buscaColetaCodCli = "";
        buscaColetaLojaCli = "";
        buscaColetaNomeCli = "";
        buscaColetaCodVend = "";
        buscaColetaQtdItens = "";
        buscaColetaValor = "";
        buscaColetaCodCondPg = "";
        buscaColetaCondPg = "";
        buscaColetaFormaPg = "";
        buscaColetaInfoAdic = "";
        buscaColetaCodBorr = "";
        buscaColetaNomeBorr = "";
        buscaColetaDocBorr = "";
        buscaColetaAssinBorr = null;
        buscaColetaAssinCli = null;
        buscaColetaStatus = "";
        buscaColetaStatusEmail = "";
        buscaColetaIdSeq = "";
        buscaColetaTipoCol = "";
        buscaColetaColetPor = "";
        buscaColetaPDFByte = null;
        buscaColetaIdUsr = "";
        buscaColetaDocCli = "";
        buscaColetaDtChegada = "";
        buscaColetaOrcamento = "";

        if (envio == 0) { //** 0 = Somente busca - 1 = Busca todos os dados para enviar para o BD **/
            if (!GettersSetters.getCodigoVendedor().equals("000") || GettersSetters.getTipoUsuario().equals("P")
                    && CheckConnection.isConnected(context)) {
                query = "SELECT " +
                        "COL_CABEC_FILIAL, " +
                        "COL_CABEC_IDENTIF, " +
                        "COL_CABEC_DATA, " +
                        "COL_CABEC_COD_CLI, " +
                        "COL_CABEC_LOJA_CLI, " +
                        "COL_CABEC_RZ_SOCIAL, " +
                        "COL_CABEC_VENDEDOR, " +
                        "COL_CABEC_QNT_ITENS, " +
                        "COL_CABEC_TOTAL, " +
                        "COL_CABEC_COD_COND_PG, " +
                        "COL_CABEC_COND_PG, " +
                        "COL_CABEC_FORMA_PG, " +
                        "COL_CABEC_INFO_ADIC, " +
                        "COL_CABEC_BORRACHEIRO, " +
                        "COL_CABEC_NOME_BORRACHEIRO, " +
                        "COL_CABEC_DOC_BORRACHEIRO, " +
                        "COL_CABEC_ASSIN_BORR BLOB, " +
                        "COL_CABEC_ASSIN_CLI BLOB, " +
                        "COL_CABEC_STATUS_ENVIO, " +
                        "COL_CABEC_STATUS_ENVIO_EMAIL, " +
                        "COL_CABEC_SEQ_IDENTIFICADOR, " +
                        "COL_CABEC_TIPO_COLETA, " +
                        "COL_CABEC_HORA_COLETA, " +
                        "COL_CABEC_COL_POR, " +
                        "COL_CABEC_PDF, " +
                        "COL_CABEC_ID_USR, " +
                        "COL_CABEC_DOC_CLI, " +
                        "COL_CABEC_DT_CHEGADA, " +
                        "COL_CABEC_ORCAMENTO, " +
                        "'' CODIGO, " +
                        "'' NOME_TOTVS, " +
                        "COL_CABEC_OFFLINE, " +
                        "COL_CABEC_CLI_NOVO " +
                        "FROM COLETA_CABEC CC " +
                        " LEFT JOIN USUARIOS_VENDEDORES UV ON UV.USRVEND_ID = CC.COL_CABEC_ID_USR " +
                        "WHERE 1 = 1 ";
                        if (GettersSetters.getTipoUsuario().equals("P")) {
                            query += " AND UV.USRVEND_ID_PERMITIDO LIKE '%" + GettersSetters.getIdUsuarioLogado().trim() + "%'";
                        } else {
                            query += "AND (COL_CABEC_ID_USR = '" + GettersSetters.getCodigoVendedor() + "' OR COL_CABEC_COL_POR = '" + GettersSetters.getCodigoVendedor() + "') ";
                        }
            } else {
                query = "SELECT " +
                        "COL_CABEC_FILIAL, " +
                        "COL_CABEC_IDENTIF, " +
                        "COL_CABEC_DATA, " +
                        "COL_CABEC_COD_CLI, " +
                        "COL_CABEC_LOJA_CLI, " +
                        "COL_CABEC_RZ_SOCIAL, " +
                        "COL_CABEC_VENDEDOR, " +
                        "COL_CABEC_QNT_ITENS, " +
                        "COL_CABEC_TOTAL, " +
                        "COL_CABEC_COD_COND_PG, " +
                        "COL_CABEC_COND_PG, " +
                        "COL_CABEC_FORMA_PG, " +
                        "COL_CABEC_INFO_ADIC, " +
                        "COL_CABEC_BORRACHEIRO, " +
                        "COL_CABEC_NOME_BORRACHEIRO, " +
                        "COL_CABEC_DOC_BORRACHEIRO, " +
                        "COL_CABEC_ASSIN_BORR BLOB, " +
                        "COL_CABEC_ASSIN_CLI BLOB, " +
                        "COL_CABEC_STATUS_ENVIO, " +
                        "COL_CABEC_STATUS_ENVIO_EMAIL, " +
                        "COL_CABEC_SEQ_IDENTIFICADOR, " +
                        "COL_CABEC_TIPO_COLETA, " +
                        "COL_CABEC_HORA_COLETA, " +
                        "COL_CABEC_COL_POR, " +
                        "COL_CABEC_PDF, " +
                        "COL_CABEC_ID_USR, " +
                        "COL_CABEC_DOC_CLI, " +
                        "COL_CABEC_DT_CHEGADA, " +
                        "COL_CABEC_ORCAMENTO, " +
                        "coalesce(USRVEND_COD_TOTVS,'') CODIGO, " +
                        "coalesce(USRVEND_NOME_TOTVS,'') NOME_TOTVS, " +
                        "COL_CABEC_OFFLINE, " +
                        "COL_CABEC_CLI_NOVO " +
                        "FROM COLETA_CABEC " +
                        "LEFT JOIN USUARIOS_VENDEDORES ON COL_CABEC_ID_USR = USRVEND_ID " +
                        "WHERE 1 = 1";
            }
        } else {
            query = "SELECT COL_CABEC_FILIAL," +
                    "COL_CABEC_IDENTIF," +
                    "COL_CABEC_DATA || ' - ' || COL_CABEC_HORA_COLETA," +
                    "COL_CABEC_COD_CLI," +
                    "COL_CABEC_LOJA_CLI," +
                    "COL_CABEC_RZ_SOCIAL," +
                    "COL_CABEC_VENDEDOR," +
                    "COL_CABEC_QNT_ITENS," +
                    "COL_CABEC_TOTAL," +
                    "COL_CABEC_COND_PG," +
                    "COL_CABEC_FORMA_PG," +
                    "COL_CABEC_INFO_ADIC," +
                    "COL_CABEC_BORRACHEIRO," +
                    "COL_CABEC_NOME_BORRACHEIRO," +
                    "COL_CABEC_DOC_BORRACHEIRO," +
                    "COL_CABEC_ASSIN_BORR," +
                    "COL_CABEC_ASSIN_CLI," +
                    "COL_CABEC_STATUS_ENVIO, " +
                    "COL_CABEC_STATUS_ENVIO_EMAIL, " +
                    "COL_CABEC_SEQ_IDENTIFICADOR, " +
                    "COL_CABEC_TIPO_COLETA, " +
                    "COL_CABEC_COL_POR, " +
                    "COL_CABEC_HORA_COLETA, " +
                    "COL_CABEC_PDF, " +
                    "COL_CABEC_ID_USR, " +
                    "COL_CABEC_DOC_CLI, " +
                    "ifnull(COL_CABEC_DT_CHEGADA,''), " +
                    "ifnull(COL_CABEC_ORCAMENTO,'F'), " +
                    "COL_CABEC_OFFLINE, " +
                    "COL_CABEC_CLI_NOVO " +
                    " FROM COLETA_CABEC " +
                    " WHERE 1 = 1 ";
        }

        if (status != 0) {
            query += " AND COL_CABEC_STATUS_ENVIO = '" + status + "'";
        }
        if (!data.equals("")) {
            if (data.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
                data = GettersSetters.converteData(data, "BR");
            }
            query += " AND COL_CABEC_DATA = '" + data + "'";
        }
        if (!numero.equals("")) {
            query += " AND trim(COL_CABEC_IDENTIF) LIKE '%" + numero.trim() + "%'";
        }
        if (!nome.equals("")) {
            query += " AND trim(COL_CABEC_RZ_SOCIAL) LIKE '%" + nome.trim() + "%'";
        }
        if (!filial.equals("")) {
            query += " AND COL_CABEC_FILIAL = '" + filial + "'";
        }

        query += " AND COL_CABEC_IDENTIF <> '' ";

//        System.out.println(query);

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    if (envio == 0) {
                        arrBuscaColetaFilial.add(res.getString(0));
                        arrBuscaNumeroColeta.add(res.getString(1));
                        arrBuscaColetaData.add(res.getString(2));
                        arrBuscaColetaCodCli.add(res.getString(3));
                        arrBuscaColetaLojaCli.add(res.getString(4));
                        arrBuscaColetaNomeCli.add(res.getString(5));
                        arrBuscaColetaCodVend.add(res.getString(6));
                        arrBuscaColetaQtdItens.add(res.getString(7));
                        arrBuscaColetaValor.add(res.getString(8));
                        arrBuscaColetaCodCondPg.add(res.getString(9));
                        arrBuscaColetaCondPg.add(res.getString(10));
                        arrBuscaColetaFormaPg.add(res.getString(11));
                        arrBuscaColetaInfoAdic.add(res.getString(12));
                        arrBuscaColetaCodBorr.add(res.getString(13));
                        arrBuscaColetaNomeBorr.add(res.getString(14));
                        arrBuscaColetaDocumBorr.add(res.getString(15));
                        arrBuscaColetaAssinBorr.add(res.getBlob(16));
                        arrBuscaColetaAssinCli.add(res.getBlob(17));
                        arrBuscaColetaStatus.add(res.getString(18));
                        arrBuscaColetaStatusEmail.add(res.getString(19));
                        arrBuscaColetaIdSeq.add(res.getString(20));
                        arrBuscaColetaTipoColeta.add(res.getString(21));
                        arrBuscaColetaHoraColeta.add(res.getString(22));
                        arrBuscaColetaColPor.add(res.getString(23));
                        arrBuscaColetaPdf.add(res.getBlob(24));
                        arrBuscaColetaIdUsr.add(res.getString(25));
                        arrBuscaColetaDocCli.add(res.getString(26));
                        arrBuscaColetaDtChegada.add(res.getString(27));
                        arrBuscaColetaOrcamento.add(res.getString(28));
                        arrBuscaColetaNomeVend.add(res.getString(30));
                        arrBuscaColetaOffline.add(res.getString(31));
                        arrBuscaColetaCliNovo.add(res.getString(32));
                    } else {
                        buscaColetaFilial = res.getString(0);
                        buscaColetaNumero = res.getString(1);
                        buscaColetaData = res.getString(2);
                        buscaColetaCodCli = res.getString(3);
                        buscaColetaLojaCli = res.getString(4);
                        buscaColetaNomeCli = res.getString(5);
                        buscaColetaCodVend = res.getString(6);
                        buscaColetaQtdItens = res.getString(7);
                        buscaColetaValor = res.getString(8);
                        buscaColetaCondPg = res.getString(9);
                        buscaColetaFormaPg = res.getString(10);
                        buscaColetaInfoAdic = res.getString(11);
                        buscaColetaCodBorr = res.getString(12);
                        buscaColetaNomeBorr = res.getString(13);
                        buscaColetaDocBorr = res.getString(14);
                        buscaColetaAssinBorr = res.getBlob(15);
                        buscaColetaAssinCli = res.getBlob(16);
                        buscaColetaStatus = res.getString(17);
                        buscaColetaStatusEmail = res.getString(18);
                        buscaColetaIdSeq = res.getString(19);
                        buscaColetaTipoCol = res.getString(20);
                        buscaColetaColetPor = res.getString(21);
                        buscaColetaHora = res.getString(22);
                        buscaColetaPDFByte = res.getBlob(23);
                        buscaColetaIdUsr = res.getString(24);
                        buscaColetaDocCli = res.getString(25);
                        buscaColetaDtChegada = res.getString(26);
                        buscaColetaOrcamento = res.getString(27);
                    }
                } while (res.moveToNext());
            }
            return res;
        }
    }

    public Cursor buscaColetaItens(String qryBuscaColItens) {
        SQLiteDatabase db = this.getReadableDatabase();

        arrBuscaColetaItFilial.clear();
        arrBuscaColetaItIdentif.clear();
        arrBuscaColetaItItem.clear();
        arrBuscaColetaItData.clear();
        arrBuscaColetaItCodCli.clear();
        arrBuscaColetaItLojaCli.clear();
        arrBuscaColetaItQtd.clear();
        arrBuscaColetaItVlrUnit.clear();
        arrBuscaColetaItVlrTotal.clear();
        arrBuscaColetaItCodPrd.clear();
        arrBuscaColetaItBitola.clear();
        arrBuscaColetaItMarca.clear();
        arrBuscaColetaItModelo.clear();
        arrBuscaColetaItSerie.clear();
        arrBuscaColetaItDot.clear();
        arrBuscaColetaItMontado.clear();
        arrBuscaColetaItDesenho.clear();
        arrBuscaColetaItUrgente.clear();
        arrBuscaColetaItPercComBorr.clear();
        arrBuscaColetaItVlrComBorr.clear();
        arrBuscaColetaItStatus.clear();
        arrBuscaColetaItObs.clear();
        arrBuscaColetaItCAgua.clear();
        arrBuscaColetaItCCamara.clear();
        arrBuscaColetaItGarantia.clear();

        try (Cursor res = db.rawQuery(qryBuscaColItens, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    arrBuscaColetaItFilial.add(res.getString(0));
                    arrBuscaColetaItIdentif.add(res.getString(1));
                    arrBuscaColetaItItem.add(res.getString(2));
                    arrBuscaColetaItData.add(res.getString(3));
                    arrBuscaColetaItCodCli.add(res.getString(4));
                    arrBuscaColetaItLojaCli.add(res.getString(5));
                    arrBuscaColetaItQtd.add(res.getString(6));
                    arrBuscaColetaItVlrUnit.add(res.getString(7));
                    arrBuscaColetaItVlrTotal.add(res.getString(8));
                    arrBuscaColetaItCodPrd.add(res.getString(9));
                    arrBuscaColetaItBitola.add(res.getString(10));
                    arrBuscaColetaItMarca.add(res.getString(11));
                    arrBuscaColetaItModelo.add(res.getString(12));
                    arrBuscaColetaItSerie.add(res.getString(13));
                    arrBuscaColetaItDot.add(res.getString(14));
                    arrBuscaColetaItMontado.add(res.getString(15));
                    arrBuscaColetaItDesenho.add(res.getString(16));
                    arrBuscaColetaItUrgente.add(res.getString(17));
                    arrBuscaColetaItPercComBorr.add(res.getString(18));
                    arrBuscaColetaItVlrComBorr.add(res.getString(19));
                    arrBuscaColetaItStatus.add(res.getString(20));
                    arrBuscaColetaItObs.add(res.getString(21));
                    arrBuscaColetaItCAgua.add(res.getString(22));
                    arrBuscaColetaItCCamara.add(res.getString(23));
                    arrBuscaColetaItGarantia.add(res.getString(24));
                } while (res.moveToNext());
            }

            return res;
        }
    }

    public Cursor buscaColetaCli(String qryBuscaCliColeta, boolean geraArquivos) {
        SQLiteDatabase db = this.getReadableDatabase();

        buscaColetaCliFilial = "";
        buscaColetaCliIdentif = "";
        buscaColetaCliData = "";
        buscaColetaCliNome = "";
        buscaColetaCliCodCli = "";
        buscaColetaCliLojali = "";
        buscaColetaCliDodCli = "";
        buscaColetaCliIe = "";
        buscaColetaCliEmail = "";
        buscaColetaCliEntEnd = "";
        buscaColetaCliEntBairro = "";
        buscaColetaCliEntMunic = "";
        buscaColetaCliEntEst = "";
        buscaColetaCliEntCep = "";
        buscaColetaCliEntDDD = "";
        buscaColetaCliEntFone = "";
        buscaColetaCliCobrEnd = "";
        buscaColetaCliCobrBairro = "";
        buscaColetaCliCobrMunic = "";
        buscaColetaCliCobrEst = "";
        buscaColetaCliCobrCep = "";
        buscaColetaCliCobrDDD = "";
        buscaColetaCliCobrFone = "";
        buscaColetaCliInfoAdic = "";
        buscaColetaCliRG = null;
        buscaColetaCliCPF = null;
        buscaColetaCliCompResid = null;
        buscaColetaCliCategoria = "";

        try (Cursor res = db.rawQuery(qryBuscaCliColeta, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    if (geraArquivos) {
                        buscaColetaCliNome = res.getString(0);
                    } else {
                        buscaColetaCliFilial = res.getString(0);
                        buscaColetaCliIdentif = res.getString(1);
                        buscaColetaCliData = res.getString(2);
                        buscaColetaCliCodCli = res.getString(3);
                        buscaColetaCliLojali = res.getString(4);
                        buscaColetaCliDodCli = res.getString(5);
                        buscaColetaCliIe = res.getString(6);
                        buscaColetaCliEmail = res.getString(7);
                        buscaColetaCliEntEnd = res.getString(8);
                        buscaColetaCliEntBairro = res.getString(9);
                        buscaColetaCliEntMunic = res.getString(10);
                        buscaColetaCliEntEst = res.getString(11);
                        buscaColetaCliEntCep = res.getString(12);
                        buscaColetaCliEntDDD = res.getString(13);
                        buscaColetaCliEntFone = res.getString(14);
                        buscaColetaCliCobrEnd = res.getString(15);
                        buscaColetaCliCobrBairro = res.getString(16);
                        buscaColetaCliCobrMunic = res.getString(17);
                        buscaColetaCliCobrEst = res.getString(18);
                        buscaColetaCliCobrCep = res.getString(19);
                        buscaColetaCliCobrDDD = res.getString(20);
                        buscaColetaCliCobrFone = res.getString(21);
                        buscaColetaCliInfoAdic = res.getString(23);
                        buscaColetaCliRG = res.getBlob(24);
                        buscaColetaCliCPF = res.getBlob(25);
                        buscaColetaCliCompResid = res.getBlob(26);
                        buscaColetaCliNome = res.getString(27);
                        buscaColetaCliCategoria = res.getString(28);
                    }
                } while (res.moveToNext());
            }
            return res;
        }
    }

    /**
     * VALIDA SE EXISTE IDENTIFICADOR JÁ INSERIDO NO SQLITE
     **/
    public Cursor selectNumColeta() {
        SQLiteDatabase db = this.getReadableDatabase();

        query = "SELECT COLETA_IDENTIFICADOR_SEQ " +
                "FROM COLETA_IDENTIFICADOR" +
                " WHERE 1 = 1 " +
                " AND trim(COLETA_IDENTIFICADOR_VEND) = '" + GettersSetters.getIdUsuarioLogado().trim() + "'";

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    res.getInt(0);
                } while (res.moveToNext());
            }
            return res;
        }
    }

    public int selectMaxNumIdentificadorColeta(String codigoVendedor) {
        SQLiteDatabase db = this.getReadableDatabase();
        identifSeq = 0;

        query = "SELECT ifnull(MAX(COLETA_IDENTIFICADOR_SEQ),'0') SEQ " +
                "FROM COLETA_IDENTIFICADOR " +
                "WHERE trim(COLETA_IDENTIFICADOR_VEND) = '" + codigoVendedor.trim() + "'";

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    identifSeq = res.getInt(0) == 0 ? 1 : (res.getInt(0) + 1);
                } while (res.moveToNext());
            }

//            if (identifSeq == 0) {
//                identifSeq = 1;
//            } else {
//                identifSeq++;
//            }

            return identifSeq;
        }
    }

    public String novoClienteColeta(String campo, String codigo, String loja, boolean insere, String numeroColeta) {
        if (!insere) {
            buscaColetaCliFilial = "";
            buscaColetaCliIdentif = "";
            buscaColetaCliData = "";
            buscaColetaCliNome = "";
            buscaColetaCliCodCli = "";
            buscaColetaCliLojali = "";
            buscaColetaCliDodCli = "";
            buscaColetaCliIe = "";
            buscaColetaCliEmail = "";
            buscaColetaCliEntEnd = "";
            buscaColetaCliEntBairro = "";
            buscaColetaCliEntMunic = "";
            buscaColetaCliEntEst = "";
            buscaColetaCliEntCep = "";
            buscaColetaCliEntDDD = "";
            buscaColetaCliEntFone = "";
            buscaColetaCliCobrEnd = "";
            buscaColetaCliCobrBairro = "";
            buscaColetaCliCobrMunic = "";
            buscaColetaCliCobrEst = "";
            buscaColetaCliCobrCep = "";
            buscaColetaCliCobrDDD = "";
            buscaColetaCliCobrFone = "";
            buscaColetaCliInfoAdic = "";
            buscaColetaCliRG = null;
            buscaColetaCliCPF = null;
            buscaColetaCliCompResid = null;
        }

        String retorno = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (campo.equals("COL_CLI_ENT_FONE")) {
            query = "SELECT COL_CLI_ENT_DDD, " + campo.toUpperCase();
        } else if (campo.equals("*")) {
            query = "SELECT COL_CLI_FILIAL, COL_CLI_IDENTIF, COL_CLI_DATA, COL_CLI_COD_CLI, COL_CLI_LOJA_CLI, \n" +
                    "       COL_CLI_DOC_CLI, COL_CLI_IE, COL_CLI_EMAIL, COL_CLI_ENT_END, COL_CLI_ENT_BAIRRO, \n" +
                    "       COL_CLI_ENT_MUNIC, COL_CLI_ENT_EST, COL_CLI_ENT_CEP, COL_CLI_ENT_DDD, COL_CLI_ENT_FONE, \n" +
                    "       COL_CLI_COBR_END, COL_CLI_COBR_BAIRRO, COL_CLI_COBR_MUNIC, COL_CLI_COBR_EST, \n" +
                    "       COL_CLI_COBR_CEP, COL_CLI_COBR_DDD, COL_CLI_COBR_FONE, COL_CLI_STATUS_ENVIO, \n" +
                    "       COL_CLI_INFO_ADIC, '' COL_CLI_RG, '' COL_CLI_CPF, '' COL_CLI_COMP_RESID, " +
                    "       COL_CLI_NOME, COL_CLI_CATEGORIA ";
        } else {
            query = "SELECT " + campo;
        }
        query += " FROM COLETA_CLI " +
                "WHERE 1 = 1 " +
                "  AND trim(COL_CLI_COD_CLI) = '" + codigo.trim() + "' " +
                "  AND trim(COL_CLI_LOJA_CLI) = '" + loja.trim() + "' ";
        if (!numeroColeta.trim().equals("")) {
            query += " AND trim(COL_CLI_IDENTIF) = '" + numeroColeta.trim() + "'";
        }

        //System.out.println(SQLSelectCliente);

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    if (!insere) {
                        if (campo.equals("COL_CLI_ENT_FONE")) {
                            retorno = res.getString(0) + res.getString(1);
                        } else {
                            retorno = res.getString(0);
                        }
                    } else {
                        if (campo.equals("*")) {
                            buscaColetaCliFilial = res.getString(0);
                            buscaColetaCliIdentif = res.getString(1);
                            buscaColetaCliData = res.getString(2);
                            buscaColetaCliCodCli = res.getString(3);
                            buscaColetaCliLojali = res.getString(4);
                            buscaColetaCliDodCli = res.getString(5);
                            buscaColetaCliIe = res.getString(6);
                            buscaColetaCliEmail = res.getString(7);
                            buscaColetaCliEntEnd = res.getString(8);
                            buscaColetaCliEntBairro = res.getString(9);
                            buscaColetaCliEntMunic = res.getString(10);
                            buscaColetaCliEntEst = res.getString(11);
                            buscaColetaCliEntCep = res.getString(12);
                            buscaColetaCliEntDDD = res.getString(13);
                            buscaColetaCliEntFone = res.getString(14);
                            buscaColetaCliCobrEnd = res.getString(15);
                            buscaColetaCliCobrBairro = res.getString(16);
                            buscaColetaCliCobrMunic = res.getString(17);
                            buscaColetaCliCobrEst = res.getString(18);
                            buscaColetaCliCobrCep = res.getString(19);
                            buscaColetaCliCobrDDD = res.getString(20);
                            buscaColetaCliCobrFone = res.getString(21);
                            buscaColetaCliInfoAdic = res.getString(23);
                            buscaColetaCliRG = new byte[0];
                            buscaColetaCliCPF = new byte[0];
                            buscaColetaCliCompResid = new byte[0];
                            buscaColetaCliNome = res.getString(27);
                        } else {
                            if (campo.contains("RG")) {
                                buscaColetaCliRG = res.getBlob(0);
                            }
                            if (campo.contains("CPF")) {
                                buscaColetaCliCPF = res.getBlob(0);
                            }
                            if (campo.contains("COMP_RESID")) {
                                buscaColetaCliCompResid = res.getBlob(0);
                            }
                        }
                    }
                } while (res.moveToNext());
            }

            return retorno;
        }
    }

    public String buscaPagamento(@NotNull String campo, String codCli, String lojaCli, String identificador, String data) {
        String retorno = null;
        SQLiteDatabase db = this.getReadableDatabase();

        if (data.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
            data = GettersSetters.converteData(data, "BR");
        }

        query = "SELECT ifnull(" + campo.toUpperCase() + ",'') " +
                "FROM COLETA_CABEC " +
                "WHERE 1 = 1 " +
                "  AND trim(COL_CABEC_COD_CLI) = '" + codCli.trim() + "' " +
                "  AND trim(COL_CABEC_LOJA_CLI) = '" + lojaCli.trim() + "' " +
                "  AND trim(COL_CABEC_IDENTIF) = '" + identificador.trim() + "' " +
                "  AND trim(COL_CABEC_DATA) = '" + data.trim() + "'";

        try (Cursor res = db.rawQuery(query, null)) {
            if (res != null && res.moveToFirst()) {
                do {
                    retorno = res.getString(0);
                } while (res.moveToNext());
            }
            return retorno;
        }
    }

    public String selecionaParametro(String parametro) {
        SQLiteDatabase db = this.getReadableDatabase();
        String retorno = "0";

        try {
            query = "SELECT PAR_CONTEUDO " +
                    "  FROM PARAMETROS " +
                    "WHERE PAR_VAR = '" + parametro + "' " +
                    "  AND trim(PAR_FIL) = ''";

//            System.out.println(query);

            try (Cursor res = db.rawQuery(query, null)) {
                if (res != null && res.moveToFirst()) {
                    do {
                        retorno = res.getString(0);
                    } while (res.moveToNext());
                }
            }

        } catch (SQLException expt) {
            expt.printStackTrace();
        }

        return retorno;
    }

//    public byte[] buscaPdfColeta(String identif) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String qry = "SELECT COL_CABEC_ASSIN_CLI " +
//                "FROM COLETA_CABEC " +
//                "WHERE COL_CABEC_IDENTIF = '" + identif + "'";
//
//        byte[] img = null;
//
//        try (Cursor res = db.rawQuery(qry, null)) {
//            if (res != null && res.moveToFirst()) {
//                do {
//                    img = res.getBlob(0);
//                } while (res.moveToNext());
//            }
//            return img;
//        }
//    }

    /**
     * DELETES
     **/
    public Integer delUsuario(String id) { //busca dos dados pela tela do admin
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("USUARIOS", "ID_USR = ?", new String[]{id});
    }

    public void delFiliais() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + FILIAIS);
    }

    public void delFiliaisUsuarios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM FILIAIS_USUARIOS");
    }

//    public void delEstados() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + ESTADOS);
//    }

    public boolean delColeta(String qryDelColetasCabec, String qryDelColetaItens, String qryDelCliColeta) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean resultado = false;

        try {
            db.execSQL(qryDelColetasCabec);
            db.execSQL(qryDelColetaItens);
            if (!qryDelCliColeta.equals("")) {
                db.execSQL(qryDelCliColeta);
            }
            resultado = true;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return resultado;
    }

    /**
     * UPDATES *
     */
    public boolean updColeta(String qryUpd) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean resultado = false;

        try {
            db.execSQL(qryUpd);
            resultado = true;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return resultado;
    }

//    public boolean updColetaInsertPdfImg(String where, String pdf) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean resultado = false;
//
//        try {
//            ContentValues cv = new ContentValues();
//            cv.put("COL_CABEC_PDF", pdf);
//            db.update("COLETA_CABEC", cv, where, null);
//            resultado = true;
//        } catch (Exception err) {
//            err.printStackTrace();
//        }
//
//        return resultado;
//    }

//    public boolean updUsuario(String qryUpd) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        boolean resultado = false;
//
//        try {
//            db.execSQL(qryUpd);
//            resultado = true;
//        } catch (Exception err) {
//            err.printStackTrace();
//        }
//
//        return resultado;
//    }
}