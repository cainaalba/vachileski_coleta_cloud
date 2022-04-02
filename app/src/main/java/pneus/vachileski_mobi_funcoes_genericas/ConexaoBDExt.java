package pneus.vachileski_mobi_funcoes_genericas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pneus.vachileski_mobi_apanhe_pneus.ColetaCliente;
import pneus.vachileski_mobi_apanhe_pneus.ColetaItens;
import pneus.vachileski_mobi_apanhe_pneus.ColetaPagamento;
import pneus.vachileski_mobi_apanhe_pneus.Home;

@SuppressWarnings({"SpellCheckingInspection", "WeakerAccess"})
public class ConexaoBDExt {
    public ResultSet resultSet = null;
    public Connection connection = null;
    public PreparedStatement preparedStatement = null;

    public String erroExcecao;
    public double contClientes = 0;
    public double contProdutos = 0;
    public double contBorracheiros = 0;
    public double contCondPg = 0;

    //VARIAVEL GLOBAL USADA EM TODAS AS QUERYES
    private String queryGeral = "";

    public String idUsuarioBdExt = null;
    public String nomeUsuarioBdExt = null;
    public String usuarioBdExt = null;
    public String senhaUsuarioBdExt = null;
    public String statusUsuarioBdExt = null;
    public String cpfUsuarioBdExt = null;
    public String dataExpiraSenhaUsuarioBdExt = null;
    public String emailUsuarioBdExt = null;
    public String tipoUsuarioBdExt = null;
    public String rgUsuarioBdExt = null;
    public String dataNascimentoUsuarioBdExt = null;
    public String opcSqLiteUsuarioBdExt = null;
    public String codTotvsUsuarioBdExt = null;
    public String nomeTotvsUsuarioBdExt = null;

    public String descricaoFilial = "";
    public String cnpjFilial = "";
    public String enderecoFilial = "";
    public String cidadeFilial = "";
    public String telefoneFilial = "";
    public String cepFilial = "";

    String connectionUrlAws = null;

    public String codigoCliente = null;
    public String lojaCliente = null;

    public String codProd = "";
    public String descrProd = "";
    public String prodBloq = "";
    public String descricaoDesenhoProd = "";
    public String precoTabelaProd = "";
    public String grupoProd = "";
    public String descontoProdVachiOst = "";
    public String descontoProdLauxen = "";

    public String codCondPgto = null;
    public String descriCondPgto = null;
    public String formaPgtoCondPgto = null;
    public String descFormaPgtoCondPgto = null;
    public String tipoCondPgto = null;
    public String condicaoPgto = null;

    public String codBorracheiro = "";
    public String nomeBorracheiro = "";
    public String docBorracheiro = "";

    //ARRAY TABELA FILIAIS
    public ArrayList<String> arrIdFilial = new ArrayList<>();
    public ArrayList<String> arrFilial = new ArrayList<>();
    public ArrayList<String> arrCodigoAuxFilial = new ArrayList<>();
    public ArrayList<String> arrRazaoSocialFilial = new ArrayList<>();
    public ArrayList<String> arrNomeFantasiaFilial = new ArrayList<>();

    //ARRAYS TABELA FILIAISUSUARIOS
    public ArrayList<String> arrIdEmpFiliaisUsuarios = new ArrayList<>();
    public ArrayList<String> arrIdFiliaisUsuarios = new ArrayList<>();
    public ArrayList<String> arrCodigoAuxiliarFiliaisUsuarios = new ArrayList<>();
    public ArrayList<String> arrRazaoSocialFiliaisUsuarios = new ArrayList<>();
    public ArrayList<String> arrIdUsrFiliaisUsuarios = new ArrayList<>();

    //ARRAY DAS CONDIÇÕES DE PAGAMENTO
    public ArrayList<String> arrCodCondPgto = new ArrayList<>();
    public ArrayList<String> arrDescriCondPgto = new ArrayList<>();
    public ArrayList<String> arrFormaCondPgto = new ArrayList<>();
    public ArrayList<String> arrDescriFormaCondPgto = new ArrayList<>();
    public ArrayList<String> arrCondPgto = new ArrayList<>();

    //ARRAY DOS BORRACHEIROS
    public ArrayList<String> arrCodigoBorracheiro = new ArrayList<>();
    public ArrayList<String> arrNomeBorracheiro = new ArrayList<>();
    public ArrayList<String> arrDocBorracheiro = new ArrayList<>();

    //ARRAYS COM COMISSÃO DE BORRACHEIROS
    public ArrayList<String> arrIdComissBorracheiro = new ArrayList<>();
    public ArrayList<String> arrFxIniComissBorracheiro = new ArrayList<>();
    public ArrayList<String> arrFxFimComissBorracheiro = new ArrayList<>();
    public ArrayList<String> arrPercComissBorracheiro = new ArrayList<>();

    //ARRAYS COM OS USUARIOS E VENDEDORES
    public ArrayList<String> arrIdUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrCodTotvsUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrNomeTotvsUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrTipoVendUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrClientesPermitidosUsuarioVendedor = new ArrayList<>();
    public ArrayList<String> arrCodBorrachPatioUsuarioVendedor = new ArrayList<>();

    @SuppressLint("NewApi")
    @SuppressWarnings("SpellCheckingInspection")
    public Connection ConnectToDatabase(String fonteBD, String conexaoBD) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        if ("C".equals(fonteBD)) {// Variável para conexão com banco de dados EXTERNO - Coleta
            connectionUrlAws = DadosBD.driverConnSQLSrv + DadosBD.endpointColetaCloud + DadosBD.paramSQLSrv + DadosBD.bdColeta;
        }

        if (conexaoBD.equals("0")) {
            try {
                GettersSetters.setConexaoBD("3");
                GettersSetters.setStringConexao("Interna");
                DriverManager.setLoginTimeout(3);
                connection = DriverManager.getConnection(connectionUrlAws);
            } catch (Exception e) {
                GettersSetters.setErroEnvioColetaBDExt("Não foi possível conectar na base de dados. Feche o aplicativo e tente novamente.");
                e.printStackTrace();
                connection = null;
            }
        } else {
            if ("3".equals(conexaoBD)) {
                try { //conexão Interna - Estabiliza a conexão
                    connection = DriverManager.getConnection(connectionUrlAws);
                } catch (Exception e) {
                    connectionUrlAws = null;
                    preparedStatement = null;
                    connection = null;
                    GettersSetters.setConexaoBD("0");
                    GettersSetters.setErroEnvioColetaBDExt("Não foi possível conectar na base de dados. Feche o aplicativo e tente novamente.");
                }
            }
        }
        return connection;
    }

    /*
     * ---------- SELECTS -------------
     */

    public ResultSet selectNovaVersao() {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

//            query = "SELECT COL_VERSAO, " +
//                    "       COL_STATUS, " +
//                    "       COL_DATA_LIB, " +
//                    "       COL_DESCR_VERSAO " +
//                    "FROM COLETA_VERSAO " +
//                    "WHERE COL_VERSAO IN (SELECT MAX(COL_VERSAO) VERSAO FROM COLETA_VERSAO WHERE COL_STATUS = 'A') " +
//                    "GROUP BY COL_VERSAO, COL_STATUS, COL_DATA_LIB, COL_DESCR_VERSAO";
//
//            stmt = con.prepareStatement(query);
            queryGeral = "EXECUTE BUSCA_VERSAO";
            preparedStatement = connection.prepareCall(queryGeral);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
            return null;
        }
    }

    public ResultSet selectVersaoApp(String versaoAtual) {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT COL_VERSAO, " +
                    "       COL_STATUS, " +
                    "       COL_DATA_LIB, " +
                    "       COL_DESCR_VERSAO, " +
                    "       (SELECT COUNT(COL_VERSAO) FROM COLETA_VERSAO WHERE COL_VERSAO > '" + versaoAtual + "') CONTA_VERSOES " +
                    "FROM COLETA_VERSAO " +
                    "WHERE COL_VERSAO = '" + versaoAtual + "' " +
                    "GROUP BY COL_VERSAO, COL_STATUS, COL_DATA_LIB, COL_DESCR_VERSAO";

            preparedStatement = connection.prepareStatement(queryGeral);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
            return null;
        }
    }

    public ResultSet selecionaAvisos() {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT COL_AVISO, COL_DATA_INI, COL_DATA_FIM " +
                    "FROM COLETA_AVISOS " +
                    "WHERE 1 = 1 " +
                    "   AND '" + GettersSetters.getDataEN() + "' BETWEEN COL_DATA_INI AND COL_DATA_FIM";

            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
            return null;
        }
    }

    public void selectFiliais() {
        arrIdFilial.clear();
        arrFilial.clear();
        arrCodigoAuxFilial.clear();
        arrRazaoSocialFilial.clear();
        arrNomeFantasiaFilial.clear();

        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

//            query = "SELECT * " +
//                    "FROM FILIAL " +
//                    "WHERE CD_AUXILIAR NOT IN ('010101','020110','020105','030101','070101')";

            queryGeral = "EXECUTE COL_BUSCA_FILIAIS";
            preparedStatement = connection.prepareCall(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                arrIdFilial.add(resultSet.getString(1));
                arrFilial.add(resultSet.getString(2));
                arrCodigoAuxFilial.add(resultSet.getString(3));
                arrRazaoSocialFilial.add(resultSet.getString(4));
                arrNomeFantasiaFilial.add(resultSet.getString(5));
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
        }
    }

    public void selectFiliaisUsuarios(String idUssuario) {
        arrIdEmpFiliaisUsuarios.clear();
        arrIdFiliaisUsuarios.clear();
        arrCodigoAuxiliarFiliaisUsuarios.clear();
        arrIdUsrFiliaisUsuarios.clear();
        arrRazaoSocialFiliaisUsuarios.clear();

        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

            queryGeral = "EXECUTE COL_BUSCA_FILIAL_USUARIO @ID = '" + idUssuario + "'";
            preparedStatement = connection.prepareCall(queryGeral);

            preparedStatement = connection.prepareCall(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                arrIdEmpFiliaisUsuarios.add(resultSet.getString(1));
                arrIdFiliaisUsuarios.add(resultSet.getString(2));
                arrCodigoAuxiliarFiliaisUsuarios.add(resultSet.getString(3));
                arrIdUsrFiliaisUsuarios.add(resultSet.getString(4));
                arrRazaoSocialFiliaisUsuarios.add(resultSet.getString(5));
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
        }
    }

    public String vendedor(String campo, String codigo) {
        String retorno = "";

        queryGeral = "SELECT " + campo.toUpperCase() + " " +
                "FROM SA3010 " +
                " WHERE A3_COD = '" + codigo + "'";

        //System.out.println(SQLUsuarios);
        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                retorno = resultSet.getString(1).trim();
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
        }
        return retorno;
    }

    public String cliente(String campo, String codigo, String loja) {
        String retorno = "";

        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            if (campo.equals("A1_TEL")) {
                queryGeral = "SELECT A1_DDD + ' ' + " + campo.toUpperCase() + " FROM SA1010 ";
            } else if (campo.equals("A1_TELEX")) {
                queryGeral = "SELECT A1_ZDDD2 + ' ' + " + campo.toUpperCase() + " FROM SA1010 ";
            } else {
                queryGeral = "SELECT ISNULL(" + campo.toUpperCase() + ",'') FROM SA1010 ";
            }
            queryGeral += "WHERE A1_COD = '" + codigo + "' " +
                    "AND A1_LOJA = '" + loja + "' " +
                    "AND D_E_L_E_T_ = '' ";

//            //System.out.println(SQLSelectCliente);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                retorno = resultSet.getString(1);
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
        }
        return retorno;
    }

    public String clienteNovo(String campo, String codigo, String loja) {
        String retorno = null;

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            if (campo.equals("COL_CLI_ENT_FONE")) {
                queryGeral = "SELECT COL_CLI_ENT_DDD + ' ' + " + campo.toUpperCase() + " FROM COLETA_CLI ";
            } else if (campo.equals("COL_CLI_COBR_FONE")) {
                queryGeral = "SELECT COL_CLI_COBR_DDD + ' ' + " + campo.toUpperCase() + " FROM COLETA_CLI ";
            } else {
                queryGeral = "SELECT " + campo.toUpperCase() + " FROM COLETA_CLI ";
            }
            queryGeral += "WHERE COL_CLI_COD_CLI = '" + codigo + "' " +
                    "AND COL_CLI_LOJA_CLI = '" + loja + "'";

            //System.out.println(SQLSelectCliente);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                retorno = resultSet.getString(1);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
        }
        return retorno;
    }


    public void selectUsuarios(String usuario, String senha, String cpf, String email, String id) {
        idUsuarioBdExt = "";
        nomeUsuarioBdExt = "";
        this.usuarioBdExt = "";
        senhaUsuarioBdExt = "";
        statusUsuarioBdExt = "";
        dataExpiraSenhaUsuarioBdExt = "";
        cpfUsuarioBdExt = "";
        emailUsuarioBdExt = "";
        tipoUsuarioBdExt = "";
        rgUsuarioBdExt = "";
        dataNascimentoUsuarioBdExt = "";
        opcSqLiteUsuarioBdExt = "";
        codTotvsUsuarioBdExt = "";
        nomeTotvsUsuarioBdExt = "";

        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

            queryGeral = "SELECT " +
                    "US.ID, " +
                    "LTRIM(RTRIM(US.NOME)) NOME, " +
                    "LTRIM(RTRIM(US.LOGIN)) LOGIN, " +
                    "LTRIM(RTRIM(US.PASSWORD)) PASS, " +
                    "ISNULL(US.STATUS,'') STATUS, " +
                    "ISNULL(US.VALID_SENHA_TEMP,'') SENHA_TEMP, " +
                    "ISNULL(SA3.A3_CGC,'') CGC, " +
                    "ISNULL(LTRIM(RTRIM(SA3.A3_EMAIL)),'') EMAIL, " +
                    "ISNULL(US.TIPO_USUARIO,'') TIPO_USR, " +
//                    "ISNULL(cod_totvs, '') CODTOTVS, " +
                    "ISNULL((SELECT A3_COD \n" +
                    "   FROM [SERVER\\SERVER].Protheus11.dbo.SA3010 \n" +
                    "      WHERE A3_COD = COD_TOTVS\n" +
                    "        AND A3_TIPOVEN IN ('V','T')),'') COD_TOTVS, " +
                    "ISNULL(SA3.A3_NOME, '') NOMETOTVS, " +
                    "ISNULL(US.CLIENTES_PERMITIDOS,'') CLIENTES_PERMITIDOS " +
                    " FROM USUARIOS US (NOLOCK) " +
                    "LEFT JOIN [SERVER\\SERVER].Protheus11.dbo.SA3010 SA3 WITH(NOLOCK) ON A3_COD = cod_totvs " +
                    "WHERE 1 = 1 ";
            queryGeral += "AND US.LOGIN = '" + usuario + "' ";
            if (!id.equals("")) {
                queryGeral += "AND US.ID = '" + id + "'";
            }
            if (!senha.equals("")) {
//                query += " AND PWDCOMPARE('"+ senha +"',PWDENCRYPT(PASSWORD)) = 1";
                queryGeral += "AND US.PASSWORD = '" + senha + "'";
            }
            if (!cpf.equals("")) {
                queryGeral += " AND SA3.A3_CGC = '" + cpf + "'";
            }
            if (!email.equals("")) {
                queryGeral += " AND LTRIM(RTRIM(US.EMAIL)) = '" + email + "'";
            }

//                System.out.println(query);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                idUsuarioBdExt = resultSet.getString(1).trim(); //id do usuário
                nomeUsuarioBdExt = resultSet.getString(2).trim(); //nome do usuário
                this.usuarioBdExt = resultSet.getString(3).trim(); //usuario
                senhaUsuarioBdExt = resultSet.getString(4).trim(); //senha do usuário
                statusUsuarioBdExt = resultSet.getString(5).trim(); //status do usuário
                dataExpiraSenhaUsuarioBdExt = resultSet.getString(6).trim(); //data expiração de senha
                cpfUsuarioBdExt = resultSet.getString(7).trim(); // cpf do usuario
                emailUsuarioBdExt = resultSet.getString(8).trim(); // email do usuario
                tipoUsuarioBdExt = resultSet.getString(9).trim(); // Tipo do Usuário
                codTotvsUsuarioBdExt = resultSet.getString(10).trim();
                nomeTotvsUsuarioBdExt = resultSet.getString(11).trim();
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
            Log.e("ExecQry Usuários", "Erro: " + e);
        }
    }

    public void insereUsuariosVendedores(Context context) {
        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();

        queryGeral = "SELECT US.ID, " +
                "       LTRIM(RTRIM(US.NOME)) NOME, " +
                "       US.STATUS, " +
                "       ISNULL(US.COD_TOTVS,'') COD_TOTVS, " +
                "       ISNULL((SELECT LTRIM(RTRIM(A3_NOME)) \n" +
                "       FROM [SERVER\\SERVER].Protheus11.dbo.SA3010 \n" +
                "           WHERE A3_COD = COD_TOTVS\n" +
                "             AND A3_TIPOVEN IN ('V','T')),'') NOME_TOTVS, " +
                "       CASE WHEN US.COD_TOTVS <> '' AND (US.TIPO_USUARIO IS NULL OR US.TIPO_USUARIO = '') " +
                "              THEN 'V' " +
                "            ELSE US.TIPO_USUARIO " +
                "       END AS TIPO_USUARIO, " +
                "       ISNULL(US.GERENTE,'') GERENTE, " +
                "       ISNULL(US.ID_PERMITIDO,'') ID_PERMITIDO, " +
                "       ISNULL(US.CLIENTES_PERMITIDOS,'') CLIENTES_PERMITIDOS, " +
                "       CASE US.TIPO_USUARIO WHEN 'P' " +
                "               THEN ISNULL(COD_BORRACHEIRO,'') " +
                "           ELSE '' " +
                "       END AS BORRACHEIRO " +
                "FROM USUARIOS US (NOLOCK) " +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.dbo.SA3010 SA3 WITH(NOLOCK) ON SA3.A3_COD = US.COD_TOTVS " +
                "                                                                 AND SA3.D_E_L_E_T_ = '' " +
                "WHERE 1 = 1 " +
                "  AND US.STATUS = 'A' " +
                "  AND US.TIPO_USUARIO <> '' " +
                "  AND US.TIPO_USUARIO <> 'NULL' " +
                "  AND US.TIPO_USUARIO IS NOT NULL " +
                "ORDER BY CAST(US.ID AS numeric) ";

//        System.out.println(queryGeral);

        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            db.beginTransaction();
            String qryInsert = "INSERT INTO USUARIOS_VENDEDORES (USRVEND_ID, USRVEND_NOME, USRVEND_COD_TOTVS, USRVEND_NOME_TOTVS, " +
                    "USRVEND_TIPO_VEND, USRVEND_GERENTE, USRVEND_ID_PERMITIDO, USRVEND_CLIENTES_PERMITIDOS, USRVEND_COD_BORRACHEIRO) " +
                    "VALUES (?,?,?,?,?,?,?,?,?);";
            SQLiteStatement dbStmt = db.compileStatement(qryInsert);
            db.execSQL("DELETE FROM USUARIOS_VENDEDORES");

            while (resultSet.next()) {
//                db.delete("USUARIOS_VENDEDORES","USRVEND_NOME_TOTVS = '' OR USRVEND_ID = ?", new String[]{resultSet.getString(1)});
                dbStmt.clearBindings();
                dbStmt.bindString(1, resultSet.getString(1)); //ID
                dbStmt.bindString(2, resultSet.getString(2).trim().toUpperCase()); //NOME
                dbStmt.bindString(3, resultSet.getString(4)); //COD TOTVS
                dbStmt.bindString(4, resultSet.getString(5)); //NOME TOTVS
                dbStmt.bindString(5, resultSet.getString(6).trim().toUpperCase()); //TIPO USUARIO
                dbStmt.bindString(6, resultSet.getString(7)); //GERENTE
                dbStmt.bindString(7, resultSet.getString(8)); //ID PERMITIDO
                dbStmt.bindString(8, resultSet.getString(9)); //CLIENTES PERMITIDOS PARA SELEÇÃO
                dbStmt.bindString(9, resultSet.getString(10)); //CÓDIGO DO BORRACHEIRO (QUANDO PÁTIO)

                dbStmt.executeInsert();
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean selectionaEInsereClientesLocal(Context context, int origem) {
        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();
        FuncoesGenericas funcoesGenericas = new FuncoesGenericas();
        boolean resultado = false;
        double posicao = 0;

        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());
            queryGeral = "INSERT INTO CLIENTES_LOCAL (CLIENTES_LOCAL_COD, CLIENTES_LOCAL_LOJA, CLIENTES_LOCAL_CGC, CLIENTES_LOCAL_NOME, " +
                    "CLIENTES_LOCAL_INSCR, CLIENTES_LOCAL_EMAIL, CLIENTES_LOCAL_END, CLIENTES_LOCAL_BAIRRO, CLIENTES_LOCAL_MUN, CLIENTES_LOCAL_CEP, " +
                    "CLIENTES_LOCAL_EST, CLIENTES_LOCAL_DDD, CLIENTES_LOCAL_TEL, CLIENTES_LOCAL_ENDCOB, CLIENTES_LOCAL_BAIRROC, CLIENTES_LOCAL_MUNC, " +
                    "CLIENTES_LOCAL_ESTC, CLIENTES_LOCAL_CEPC, CLIENTES_LOCAL_DDDC, CLIENTES_LOCAL_TELC, CLIENTES_LOCAL_MSBLQL, CLIENTES_LOCAL_COND, " +
                    "CLIENTES_LOCAL_ZCATCLI, CLIENTES_LOCAL_TABELA, CLIENTES_LOCAL_RECNO, CLIENTES_LOCAL_CLI_GRP, CLIENTES_LOCAL_DESC_EXCL, CLIENTES_LOCAL_BORR) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement dbStmt = db.compileStatement(queryGeral);

            queryGeral = "SELECT \n" +
                    "  A1_COD, \n" +
                    "  A1_LOJA, \n" +
                    "  A1_CGC, \n" +
                    "  A1_NOME, \n" +
                    "  A1_INSCR, \n" +
                    "  A1_EMAIL, \n" +
                    "  A1_END, \n" +
                    "  A1_BAIRRO, \n" +
                    "  A1_MUN, \n" +
                    "  A1_CEP, \n" +
                    "  A1_EST, \n" +
                    "  A1_DDD, \n" +
                    "  A1_TEL, \n" +
                    "  A1_ENDCOB, \n" +
                    "  A1_BAIRROC, \n" +
                    "  A1_MUNC, \n" +
                    "  A1_ESTC, \n" +
                    "  A1_CEPC, \n" +
                    "  A1_ZDDD2, \n" +
                    "  A1_TELEX, \n" +
                    "  A1_MSBLQL, \n" +
                    "  CASE A1_TABELA \n" +
                    "    WHEN '' THEN A1_COND \n" +
                    "    ELSE COALESCE((SELECT \n" +
                    "        DA0_CONDPG \n" +
                    "      FROM DA0010 DA0 (NOLOCK) \n" +
                    "      LEFT JOIN SE4010(NOLOCK) ON E4_CODIGO = DA0_CONDPG \n" +
                    "                              AND SE4010.D_E_L_E_T_ = '' \n" +
                    "                              AND E4_MSBLQL <> '1' \n" +
                    "      WHERE 1 = 1 \n" +
                    "      AND DA0.D_E_L_E_T_ = '' \n" +
                    "      AND SE4010.D_E_L_E_T_ = '' \n" +
                    "      AND ((CAST(DA0_DATATE AS date) > CAST(GETDATE() AS date) \n" +
                    "      AND DA0_DATATE <> '') \n" +
                    "      OR DA0_DATATE = '') \n" +
                    "      AND DA0_CODTAB = A1_TABELA), A1_COND) \n" +
                    "  END AS A1_COND, \n" +
                    "  A1_ZCATCLI, \n" +
                    "  CASE A1_TABELA \n" +
                    "    WHEN '' THEN '' \n" +
                    "    ELSE COALESCE((SELECT \n" +
                    "        DA0_CODTAB \n" +
                    "      FROM DA0010 DA0 (NOLOCK) \n" +
                    "      WHERE 1 = 1 \n" +
                    "        AND DA0.D_E_L_E_T_ = '' \n" +
                    "        AND ((CAST(DA0_DATATE AS date) > CAST(GETDATE() AS date) \n" +
                    "        AND DA0_DATATE <> '') OR DA0_DATATE = '') \n" +
                    "        AND DA0_CODTAB = A1_TABELA), '') \n" +
                    "  END AS A1_TABELA, \n" +
                    "  SA1.R_E_C_N_O_, \n" +
                    "  A1_ZIDENTC, \n" +
                    "  A1_ZDSCPRD, \n" +
                    "  A1_ZBORR \n" +
                    "FROM SA1010 SA1 WITH (NOLOCK) \n" +
                    "WHERE 1 = 1  \n" +
                    "  AND SA1.D_E_L_E_T_ = '' \n" +
                    "ORDER BY A1_NOME";

            //System.out.println(SQLSelectCliente);

            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            try {
                resultSet.last();
                contClientes = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
                contClientes = 0;
            }

            if (contClientes > 0) {
                db.delete("CLIENTES_LOCAL", null, null);
                db.beginTransaction();
                while (resultSet.next()) {
                    posicao += 1;

                    dbStmt.clearBindings();
                    dbStmt.bindString(1, funcoesGenericas.cripto(resultSet.getString(1).trim()));
                    dbStmt.bindString(2, funcoesGenericas.cripto(resultSet.getString(2).trim()));
                    dbStmt.bindString(3, funcoesGenericas.cripto(resultSet.getString(3).trim()));
                    dbStmt.bindString(4, funcoesGenericas.cripto(resultSet.getString(4).trim()));
                    dbStmt.bindString(5, funcoesGenericas.cripto(resultSet.getString(5).trim()));
                    dbStmt.bindString(6, funcoesGenericas.cripto(resultSet.getString(6).trim()));
                    dbStmt.bindString(7, funcoesGenericas.cripto(resultSet.getString(7).trim()));
                    dbStmt.bindString(8, funcoesGenericas.cripto(resultSet.getString(8).trim()));
                    dbStmt.bindString(9, funcoesGenericas.cripto(resultSet.getString(9).trim()));
                    dbStmt.bindString(10, funcoesGenericas.cripto(resultSet.getString(10).trim()));
                    dbStmt.bindString(11, funcoesGenericas.cripto(resultSet.getString(11).trim()));
                    dbStmt.bindString(12, funcoesGenericas.cripto(resultSet.getString(12).trim()));
                    dbStmt.bindString(13, funcoesGenericas.cripto(resultSet.getString(13).trim()));
                    dbStmt.bindString(14, funcoesGenericas.cripto(resultSet.getString(14).trim()));
                    dbStmt.bindString(15, funcoesGenericas.cripto(resultSet.getString(15).trim()));
                    dbStmt.bindString(16, funcoesGenericas.cripto(resultSet.getString(16).trim()));
                    dbStmt.bindString(17, funcoesGenericas.cripto(resultSet.getString(17).trim()));
                    dbStmt.bindString(18, funcoesGenericas.cripto(resultSet.getString(18).trim()));
                    dbStmt.bindString(19, funcoesGenericas.cripto(resultSet.getString(19).trim()));
                    dbStmt.bindString(20, funcoesGenericas.cripto(resultSet.getString(20).trim()));
                    dbStmt.bindString(21, funcoesGenericas.cripto(resultSet.getString(21).trim()));
                    dbStmt.bindString(22, funcoesGenericas.cripto(resultSet.getString(22).trim()));
                    dbStmt.bindString(23, funcoesGenericas.cripto(resultSet.getString(23).trim()));
                    dbStmt.bindString(24, funcoesGenericas.cripto(resultSet.getString(24).trim()));
                    dbStmt.bindString(25, funcoesGenericas.cripto(resultSet.getString(25).trim()));
                    dbStmt.bindString(26, funcoesGenericas.cripto(resultSet.getString(26).trim()));
                    dbStmt.bindString(27, funcoesGenericas.cripto(resultSet.getString(27).trim()));
                    dbStmt.bindString(28, funcoesGenericas.cripto(resultSet.getString(28).trim()));
                    dbStmt.execute();

                    if (origem == 0) {
                        Home.mProgressDialog.setProgress((int) ((100 / contClientes) * posicao));
                    } else {
                        ColetaCliente.mProgressDialog.setProgress((int) ((100 / contClientes) * posicao));
                    }
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            resultado = true;

            preparedStatement.close();
            connection.close();
        } catch (SQLException exptSA1) {
            db.close();
            erroExcecao = exptSA1.toString();
            exptSA1.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(exptSA1.getMessage());
            resultado = false;
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
        }

        db.close();
        return resultado;
    }

    public void selectUsuariosVendedores(String idUsuario, String tipoUsuario) {
        arrIdUsuarioVendedor.clear();
        arrUsuarioVendedor.clear();
        arrCodTotvsUsuarioVendedor.clear();
        arrNomeTotvsUsuarioVendedor.clear();
        arrTipoVendUsuarioVendedor.clear();
        arrClientesPermitidosUsuarioVendedor.clear();
        arrCodBorrachPatioUsuarioVendedor.clear();

        queryGeral = "SELECT " +
                "       ID, " +
                "       LTRIM(RTRIM(NOME)) NOME, " +
                "       ISNULL((SELECT A3_COD \n" +
                "                 FROM [SERVER\\SERVER].Protheus11.dbo.SA3010 \n" +
                "                   WHERE A3_COD = COD_TOTVS\n" +
                "                     AND A3_TIPOVEN IN ('V','T')),'') COD_TOTVS, " +
                "       LTRIM(RTRIM(A3_NOME)) NOME_TOTVS, " +
                "       CASE WHEN COD_TOTVS <> '' AND (TIPO_USUARIO IS NULL OR TIPO_USUARIO = '') " +
                "               THEN 'V' " +
                "           ELSE TIPO_USUARIO " +
                "       END AS TIPO_USUARIO, " +
                "       ISNULL(CLIENTES_PERMITIDOS,'') CLIENTES_PERMITIDOS, " +
                "       CASE US.TIPO_USUARIO WHEN 'P' " +
                "               THEN ISNULL(COD_BORRACHEIRO,'') " +
                "           ELSE '' " +
                "       END AS BORRACHEIRO " +
                "FROM USUARIOS " +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.dbo.SA3010 WITH(NOLOCK) ON A3_COD = COD_TOTVS " +
                "WHERE COD_TOTVS <> '' " +
                "  AND STATUS = 'A' ";
        if (tipoUsuario.trim().equals("G")) {
            queryGeral += "AND GERENTE = '" + idUsuario + "' ";
        } else if (tipoUsuario.trim().equals("P") || tipoUsuario.trim().equals("T")) {
            queryGeral += "AND ID_PERMITIDO LIKE ('%" + idUsuario + "%') ";
//                    " OR USRVEND_COD_TOTVS <> '')";
        } else {
            queryGeral += " AND COD_TOTVS <> '' " +
                    " AND TIPO_USUARIO IN ('V','T','P') ";
        }
        queryGeral += "ORDER BY NOME ";

        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                arrIdUsuarioVendedor.add(resultSet.getString(1));
                arrUsuarioVendedor.add(resultSet.getString(2));
                arrCodTotvsUsuarioVendedor.add(resultSet.getString(3));
                arrNomeTotvsUsuarioVendedor.add(resultSet.getString(4));
                arrTipoVendUsuarioVendedor.add(resultSet.getString(5));
                arrClientesPermitidosUsuarioVendedor.add(resultSet.getString(6));
                arrCodBorrachPatioUsuarioVendedor.add(resultSet.getString(7));
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void selectBorracheiro(Context context, String nomeBorracheiro, String docBorracheiro, boolean insereLocal) {
        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();
        int posicao = 0;

        arrCodigoBorracheiro.clear();
        arrNomeBorracheiro.clear();
        arrDocBorracheiro.clear();

        codBorracheiro = null;
        this.nomeBorracheiro = null;
        this.docBorracheiro = null;

        ConnectToDatabase("P", GettersSetters.getConexaoBD());

        db.delete("BORRACHEIROS_LOCAL", null, null);
        db.delete("COLETA_COMISS_BORR", null, null);

        String qryInsert = "INSERT INTO BORRACHEIROS_LOCAL (BORRACH_LOCAL_COD, BORRACH_LOCAL_NOME, BORRACH_LOCAL_DOC) VALUES (?,?,?);";
        SQLiteStatement dbStmt = db.compileStatement(qryInsert);

        queryGeral = "SELECT " +
                "       A3_COD, " +
                "       LTRIM(RTRIM(A3_NOME)), " +
                "       LTRIM(RTRIM(A3_CGC)), " +
                "       A3_MSBLQL, " +
                "       A3_TIPOVEN " +
                "FROM SA3010 WITH(NOLOCK) " +
                " WHERE 1 = 1";

        if (!insereLocal) {
            if (nomeBorracheiro != null) {
                queryGeral += "AND A3_NOME LIKE '%" + nomeBorracheiro + "%' ";
            } else if (docBorracheiro != null) {
                queryGeral += "AND A3_CGC = '" + docBorracheiro + "' ";
            }
        }
        queryGeral += "AND A3_TIPOVEN = 'B' " +
                "AND SA3010.D_E_L_E_T_ = '' " +
                "AND A3_MSBLQL = '2' " +
                "ORDER BY A3_NOME";

        try {
            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            try {
                resultSet.last();
                contBorracheiros = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
                contBorracheiros = 0;
            }

            //System.out.println(SQLSelectBorracheiros);
            db.beginTransaction();
            while (resultSet.next()) {
                if (insereLocal) {
                    posicao += 1;

                    dbStmt.clearBindings();
                    dbStmt.bindString(1, resultSet.getString(1));
                    dbStmt.bindString(2, resultSet.getString(2));
                    dbStmt.bindString(3, resultSet.getString(3));
                    dbStmt.execute();

                    Home.mProgressDialog.setProgress((int) ((100 / contBorracheiros) * posicao));
                } else {
                    codBorracheiro = resultSet.getString(1);
                    this.nomeBorracheiro = resultSet.getString(2);
                    this.docBorracheiro = resultSet.getString(3);

                    arrCodigoBorracheiro.add(resultSet.getString(1));
                    arrNomeBorracheiro.add(resultSet.getString(2));
                    arrDocBorracheiro.add(resultSet.getString(3));
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            preparedStatement.close();
            connection.close();

        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        /* COMISSÃO DE BORRACHEIRO **/
        if (insereLocal) {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            /* CONTAGEM **/
            contBorracheiros = 0;
            queryGeral = "SELECT COUNT(*) FROM COLETA_COMISS_BORR";
            try {
                preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    contBorracheiros = resultSet.getInt(1);
                }

                /* GET DADOS **/
                queryGeral = "SELECT * FROM COLETA_COMISS_BORR";
                preparedStatement = connection.prepareStatement(queryGeral);
                resultSet = preparedStatement.executeQuery();
                Home.mProgressDialog.setProgress(0);
                posicao = 0;

                queryGeral = "INSERT INTO COLETA_COMISS_BORR (COLETA_COMISS_ID, COLETA_COMISS_FX_INI, COLETA_COMISS_FX_FIM, " +
                        "COLETA_COMISS_PERC_COMISS) VALUES (?,?,?,?);";
                dbStmt = db.compileStatement(queryGeral);

                //valoresBorrachLocal = new ContentValues();
                db.beginTransaction();
                while (resultSet.next()) {
                    posicao++;

                    dbStmt.clearBindings();
                    dbStmt.bindString(1, resultSet.getString(1));
                    dbStmt.bindString(2, resultSet.getString(2));
                    dbStmt.bindString(3, resultSet.getString(3));
                    dbStmt.bindString(4, resultSet.getString(4));

                    dbStmt.execute();
                    Home.mProgressDialog.setProgress((int) ((100 / contBorracheiros) * posicao));
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();

                preparedStatement.close();
                connection.close();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
    }

    public void updateParametros(Context context) {
        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();
        SQLiteStatement dbStmt;
        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            queryGeral = "SELECT " +
                    "   X6_FIL, " +
                    "   X6_VAR, " +
                    "   LTRIM(RTRIM(X6_DESCRIC)) + LTRIM(RTRIM(X6_DESC1)) + LTRIM(RTRIM(X6_DESC2)) DESCRICAO, " +
                    "   X6_CONTEUD " +
                    "FROM SX6010 " +
                    "WHERE X6_VAR IN ('VK_CMBRMNT','VK_VDEDCBR','VK_PDESCGR') " +
                    "  AND D_E_L_E_T_ = '' " +
                    "  AND X6_FIL = ''";

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            db.delete("PARAMETROS", null, null);

            String qryInsert = "INSERT INTO PARAMETROS (PAR_FIL, PAR_VAR, PAR_DESCR, PAR_CONTEUDO) VALUES (?,?,?,?);";
            dbStmt = db.compileStatement(qryInsert);

            db.beginTransaction();
            while (resultSet.next()) {
                dbStmt.clearBindings();
                dbStmt.bindString(1, resultSet.getString(1));
                dbStmt.bindString(2, resultSet.getString(2));
                dbStmt.bindString(3, resultSet.getString(3));
                dbStmt.bindString(4, resultSet.getString(4));
                dbStmt.execute();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            preparedStatement.close();
            connection.close();

        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public boolean selecionaEInsereProdutos(Context context, int origem) {
        codProd = "";
        descrProd = "";
        prodBloq = "";
        descricaoDesenhoProd = "";
        precoTabelaProd = "";
        grupoProd = "";
        descontoProdVachiOst = "";
        descontoProdLauxen = "";

        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();
        ConnectToDatabase("P", GettersSetters.getConexaoBD());

        boolean resultado = false;
        double posicao = 0;

        db.delete("PRODUTOS_LOCAL", null, null);
        queryGeral = "INSERT INTO PRODUTOS_LOCAL (PRODUTOS_LOCAL_CODIGO, PRODUTOS_LOCAL_DESCR, PRODUTOS_LOCAL_BLQ, " +
                "PRODUTOS_LOCAL_DESENHO, PRODUTOS_LOCAL_PRECO, PRODUTOS_LOCAL_TIPO_GRP, PRODUTOS_LOCAL_GRUPO, PRODUTOS_LOCAL_DESC_VACHIOST, " +
                "PRODUTOS_LOCAL_DESC_LAUXEN, PRODUTOS_LOCAL_ISDESC_GRP) VALUES (?,?,?,?,?,?,?,?,?,?);";

        SQLiteStatement dbStmt = db.compileStatement(queryGeral);

        queryGeral = "SELECT LTRIM(RTRIM(B1_COD)) COD, LTRIM(RTRIM(B1_DESC)) DESCR, B1_MSBLQL, " +
                "        BS_CODIGO, BS_BASE, " +
                "        CASE BS_DESCPRD WHEN '0' THEN (CASE WHEN (B1_DESC LIKE '%VULC%') THEN 'VULC' ELSE 'SEM DESENHO' END) ELSE BS_DESCPRD END AS DESENHO, " +
                "        B1_PRV1, B1_ZTIPGRU, B1_GRUPO, COALESCE(BM_ZGRPPM1,'0') DESC1, COALESCE(BM_ZGRPPM3,'0') DESC2, B1_ZISDESC " +
                "FROM SB1010 WITH(NOLOCK) " +
                "JOIN SBS010 WITH(NOLOCK) ON BS_BASE = SUBSTRING(B1_COD, 1, 2) " +
                "           AND BS_CODIGO = SUBSTRING(B1_COD, 10, 4) " +
                "           AND SBS010.D_E_L_E_T_ = '' " +
                "LEFT JOIN SBM010 WITH(NOLOCK) ON B1_GRUPO = BM_GRUPO " +
                "WHERE 1 = 1 " +
                "  AND SB1010.D_E_L_E_T_ = '' " +
                "  AND SBS010.D_E_L_E_T_ = '' " +
                "  AND SBM010.D_E_L_E_T_ = '' " +
                "  AND B1_ZTIPGRU NOT IN ('CO','MP','IN','OU') " +
                "  AND B1_ZTIPGRU IN ('SE','PC') " +
                "  AND (B1_COD LIKE 'G1%' OR B1_COD LIKE 'G2%') " +
                "GROUP BY B1_COD, B1_DESC, B1_MSBLQL, BS_CODIGO, BS_BASE, BS_DESCPRD, B1_PRV1, B1_ZTIPGRU, B1_GRUPO, BM_ZGRPPM1, BM_ZGRPPM3, B1_ZISDESC " +
                "UNION ALL " +
                "SELECT LTRIM(RTRIM(B1_COD)) COD, LTRIM(RTRIM(B1_DESC)) DESCR, B1_MSBLQL, " +
                "       '' BS_CODIGO, '' BS_BASE, '' BS_DESCPRD, B1_PRV1, B1_ZTIPGRU, B1_GRUPO, " +
                "       COALESCE(BM_ZGRPPM1,'0') DESC1, COALESCE(BM_ZGRPPM3,'0') DESC2, B1_ZISDESC " +
                "FROM SB1010 WITH(NOLOCK) " +
                "LEFT JOIN SBM010 WITH(NOLOCK) ON B1_GRUPO = BM_GRUPO " +
                "WHERE 1 = 1 " +
                "  AND SB1010.D_E_L_E_T_ = '' " +
                "  AND SBM010.D_E_L_E_T_ = '' " +
                "  AND ((B1_ZTIPGRU NOT IN ('CO','MP','IN','OU','SE','PC') AND B1_COD NOT LIKE ('UC%')) OR B1_COD LIKE ('OS%') OR B1_GRUPO = '0220') " +
                "GROUP BY B1_COD, B1_DESC, B1_MSBLQL, B1_PRV1, B1_ZTIPGRU, B1_GRUPO, BM_ZGRPPM1, BM_ZGRPPM3, B1_ZISDESC";

        //System.out.println(SQLSelectProduto);

        try {
            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            try {
                resultSet.last();
                contProdutos = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
                contProdutos = 0;
            }

            db.beginTransaction();
            while (resultSet.next()) {
                posicao++;

                precoTabelaProd = resultSet.getString(7);

                if (resultSet.getString(7).trim().isEmpty()) {
                    precoTabelaProd = "0";
                }

                dbStmt.clearBindings();
                dbStmt.bindString(1, resultSet.getString(1));
                dbStmt.bindString(2, resultSet.getString(2));
                dbStmt.bindString(3, resultSet.getString(3));
                dbStmt.bindString(4, resultSet.getString(6));
                dbStmt.bindString(5, precoTabelaProd);
                dbStmt.bindString(6, resultSet.getString(8));
                dbStmt.bindString(7, resultSet.getString(9));
                dbStmt.bindString(8, resultSet.getString(10));
                dbStmt.bindString(9, resultSet.getString(11));
                dbStmt.bindString(10, resultSet.getString(12));

                dbStmt.execute();
                if (origem == 0) {
                    Home.mProgressDialog.setProgress((int) ((100 / contProdutos) * posicao));
                } else {
                    ColetaItens.mProgressDialogItens.setProgress((int) ((100 / contProdutos) * posicao));
                }

            }
            db.setTransactionSuccessful();
            db.endTransaction();

            contProdutos = 0;

            /* INSERÇÃO TABELAS **/
            queryGeral = "SELECT " +
                    "        DA1_ITEM, " +
                    "        DA1_CODTAB, " +
                    "        DA1_CODPRO, " +
                    "        DA1_PRCVEN " +
                    " FROM DA1010 WITH(NOLOCK) " +
                    " LEFT JOIN SB1010 WITH(NOLOCK) ON B1_COD = DA1_CODPRO " +
                    "WHERE DA1_ATIVO = '1'" +
                    "  AND DA1010.D_E_L_E_T_ = '' " +
                    "  AND SB1010.D_E_L_E_T_ = '' " +
                    "  AND B1_MSBLQL IN ('','2') " +
                    "  AND B1_ZTIPGRU NOT IN ('CO','MP','IN','OU') " +
                    "GROUP BY DA1_ITEM, DA1_CODTAB, DA1_CODPRO, DA1_PRCVEN ";

            //System.out.println(SQLSelectProduto);

            try {
                posicao = 0;
                preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = preparedStatement.executeQuery();

                try {
                    resultSet.last();
                    contProdutos = resultSet.getRow();
                    resultSet.beforeFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                    contProdutos = 0;
                }

                if (origem == 0) {
                    Home.mProgressDialog.setProgress(0);
                } else {
                    ColetaItens.mProgressDialogItens.setProgress(0);
                }

                db.delete("TAB_PRECO_LOCAL", null, null);

                queryGeral = "INSERT INTO TAB_PRECO_LOCAL (TAB_PRECO_LOCAL_ITEM, TAB_PRECO_LOCAL_COD_TAB, " +
                        "TAB_PRECO_LOCAL_COD_PRD, TAB_PRECO_LOCAL_PRECO) VALUES (?,?,?,?);";
                dbStmt = db.compileStatement(queryGeral);

                db.beginTransaction();
                while (resultSet.next()) {
                    posicao++;

                    dbStmt.clearBindings();
                    dbStmt.bindString(1, resultSet.getString(1));
                    dbStmt.bindString(2, resultSet.getString(2));
                    dbStmt.bindString(3, resultSet.getString(3));
                    dbStmt.bindString(4, resultSet.getString(4));

                    dbStmt.execute();

                    if (origem == 0) {
                        Home.mProgressDialog.setProgress((int) ((100 / contProdutos) * posicao));
                    } else {
                        ColetaItens.mProgressDialogItens.setProgress((int) ((100 / contProdutos) * posicao));
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                resultado = true;

            } catch (SQLException exp) {
                exp.printStackTrace();
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException exptSB1) {
            erroExcecao = exptSB1.toString();
            exptSB1.printStackTrace();
            resultado = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return resultado;
    }

    public void selectCondPagto(String codigoCond, String formaPto, boolean atualiza, Context context) {
        ConexaoBDInt conexaoBDInt = new ConexaoBDInt(context);
        SQLiteDatabase db = conexaoBDInt.getWritableDatabase();
        double posicao = 0;
        SQLiteStatement dbStmt;

        arrCodCondPgto.clear();
        arrDescriCondPgto.clear();
        arrFormaCondPgto.clear();
        arrDescriFormaCondPgto.clear();
        arrCondPgto.clear();

        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            queryGeral = "SELECT DISTINCT " +
                    "                   LTRIM(RTRIM(E4_CODIGO)), " +
                    "                   LTRIM(RTRIM(E4_DESCRI)), " +
                    "                   LTRIM(RTRIM(E4_FORMA)), " +
                    "                   LTRIM(RTRIM(X5_DESCRI)), " +
                    "                   LTRIM(RTRIM(E4_TIPO)), " +
                    "                   LTRIM(RTRIM(E4_COND))," +
                    "                   LTRIM(RTRIM(E4_ZUSEMPR))," +
                    "                   LTRIM(RTRIM(E4_ZFLCLT1)), " + //PREENCHIDO COM AS FILIAIS QUE USARÃO TAL CONDIÇÃO DE PAGAMNETO, SE FOR UMA CONDIÇÃO ESPECÍFICA
                    "                   E4_ZLCOLET " +
                    "FROM SE4010 WITH(NOLOCK) " +
                    "JOIN SX5010 WITH(NOLOCK) ON X5_TABELA = '24' " +
                    "           AND X5_CHAVE = E4_FORMA " +
                    "WHERE 1 = 1 " +
                    "  AND SE4010.D_E_L_E_T_ = '' " +
                    "  AND SX5010.D_E_L_E_T_ = '' " +
                    "  AND E4_TIPO <> '9' " +
                    "  AND E4_TIPO <> '5' " +
                    "  AND E4_MSBLQL <> '1' " +
                    "  AND E4_ZLCOLET = 'T' " +
                    //"  AND E4_CODIGO NOT IN ('001','P31','P20','P21','D00','D40','001') " +
                    "  AND E4_FORMA IN ('BOL','R$','DB','CC') ";

            if (formaPto.equals("")) {
                queryGeral += "AND E4_FORMA <> '' ";
            } else {
                queryGeral += "AND E4_FORMA = '" + formaPto + "' ";
            }
            if (!codigoCond.equals("0")) {
                queryGeral += "AND E4_CODIGO = '" + codigoCond + "'";
            }

            queryGeral += "GROUP BY " +
                    "           LTRIM(RTRIM(E4_CODIGO)), " +
                    "           LTRIM(RTRIM(E4_DESCRI)), " +
                    "           LTRIM(RTRIM(E4_FORMA)), " +
                    "           LTRIM(RTRIM(X5_DESCRI)), " +
                    "           LTRIM(RTRIM(E4_TIPO)), " +
                    "           LTRIM(RTRIM(E4_COND)), " +
                    "           LTRIM(RTRIM(E4_ZUSEMPR))," +
                    "           LTRIM(RTRIM(E4_ZFLCLT1)), " +
                    "           E4_ZLCOLET ";

//            System.out.println(query);

            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            db.delete("COND_PAG_LOCAL", null, null);

            queryGeral = "INSERT INTO COND_PAG_LOCAL (COND_PAG_LOCAL_COD, COND_PAG_LOCAL_DESC, COND_PAG_LOCAL_FORMA, " +
                    "COND_PAG_LOCAL_DESC_FORMA, COND_PAG_LOCAL_TIPO, COND_PAG_LOCAL_COND, COND_PAG_LOCAL_USO_EMPR, COND_PAG_LOCAL_USO_FILIAL) " +
                    "VALUES (?,?,?,?,?,?,?,?);";
            dbStmt = db.compileStatement(queryGeral);

            db.beginTransaction();

            try {
                resultSet.last();
                contCondPg = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
                contCondPg = 0;
            }

            while (resultSet.next()) {
                if (atualiza) {
                    posicao++;

                    assert dbStmt != null;
                    dbStmt.clearBindings();
                    dbStmt.bindString(1, resultSet.getString(1));
                    dbStmt.bindString(2, resultSet.getString(2));
                    dbStmt.bindString(3, resultSet.getString(3));
                    dbStmt.bindString(4, resultSet.getString(4));
                    dbStmt.bindString(5, resultSet.getString(5));
                    dbStmt.bindString(6, resultSet.getString(6));
                    dbStmt.bindString(7, resultSet.getString(7));
                    dbStmt.bindString(8, resultSet.getString(8));

                    dbStmt.execute();

                    if (context.toString().contains("ColetaPagamento")) {
                        ColetaPagamento.mProgressDialog.setProgress((int) ((100 / contCondPg) * posicao));
                    } else {
                        Home.mProgressDialog.setProgress((int) ((100 / contCondPg) * posicao));
                    }
                } else {
                    codCondPgto = resultSet.getString(1);
                    descriCondPgto = resultSet.getString(2);
                    formaPgtoCondPgto = resultSet.getString(3);
                    descFormaPgtoCondPgto = resultSet.getString(4);
                    tipoCondPgto = resultSet.getString(5);
                    condicaoPgto = resultSet.getString(6);

                    arrCodCondPgto.add(codCondPgto);
                    arrDescriCondPgto.add(descriCondPgto);
                    arrFormaCondPgto.add(formaPgtoCondPgto);
                    arrDescriFormaCondPgto.add(descFormaPgtoCondPgto);
                    arrCondPgto.add(condicaoPgto);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet condPgto(String codigo) {
        queryGeral = "SELECT E4_CODIGO, E4_COND, E4_DESCRI " +
                "FROM SE4010 WITH(NOLOCK) " +
                "WHERE 1 = 1 " +
                "  AND E4_CODIGO = '" + codigo + "'" +
                "  AND D_E_L_E_T_ = ''";
        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] buscaAssinaturas(String campo, String codCli, String lojaCli, String
            identificador, String data) {
        byte[] assinatura = new byte[0];

        queryGeral = "SELECT " + campo.toUpperCase() + " " +
                "FROM COLETA_CABEC " +
                "WHERE COL_CABEC_COD_CLI = '" + codCli + "' " +
                "  AND COL_CABEC_LOJA_CLI = '" + lojaCli + "' " +
                "  AND COL_CABEC_IDENTIF = '" + identificador + "' " +
                "  AND COL_CABEC_DATA = '" + data + "'";

        //System.out.println(qryBuscaAssinatudas);

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                assinatura = resultSet.getBytes(1);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assinatura;
    }

    public String selecionaColetaJaEnviada(String numero, String codVend, String filial) {
        String numeroColetaRet = "";

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT ISNULL(COL_CABEC_IDENTIF,'') COL_CABEC_IDENTIF " +
                    " FROM COLETA_CABEC " +
                    " WHERE COL_CABEC_IDENTIF = '" + numero + "'" +
                    "   AND (COL_CABEC_COLETADO_POR = '" + codVend + "' OR COL_CABEC_COD_CRECAP = '" + codVend + "') " +
                    "   AND COL_CABEC_FILIAL = '" + filial + "'";

            //System.out.println(SQLSelecionaColetaEnviada);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                numeroColetaRet = resultSet.getString(1);
            }

            preparedStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return numeroColetaRet;
    }

    public ResultSet buscaColetasReenvio(String numeroColeta, String status) {
        queryGeral = "SELECT COL_CABEC_FILIAL, \n" +
                "\t     COL_CABEC_IDENTIF, \n" +
                "\t     COL_CABEC_DATA, \n" +
                "\t     COL_CABEC_COD_CLI,\n" +
                "\t     COL_CABEC_LOJA_CLI,\n" +
                "\t     COL_CABEC_STATUS_ENVIO\n" +
                "FROM COLETA_CABEC\n" +
                "WHERE 1 = 1 " +
                "  AND COL_CABEC_IDENTIF = '" + numeroColeta + "'";
        if (!status.equals("")) {
            queryGeral += "  AND COL_CABEC_STATUS_ENVIO = '" + status + "' ";
        } else {
            queryGeral += "  AND COL_CABEC_STATUS_ENVIO <> '99' ";
        }

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            return preparedStatement.executeQuery();
        } catch (SQLException expt) {
            expt.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(expt.getMessage());
            return null;
        }

    }

    public ResultSet buscaColetasExtCabec(String dataColeta, String numeroColeta, String nomeCliColeta, String codFilial, String codVend, boolean reenvio,
                                          String status, String opcaoSelecionadaBusca) {
        queryGeral = "SELECT " +
                "       ISNULL(COLETA.COL_CABEC_FILIAL,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_IDENTIF,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_DATA,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_COD_CLI,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_LOJA_CLI,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_RZ_SOCIAL,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_VENDEDOR,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_QNT_ITENS,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_TOTAL,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_COND_PG,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_FORMA_PG,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_INFO_ADIC,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_BORRACHEIRO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_NOME_BORRACHEIRO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_DOC_BORRACHEIRO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_ASSIN_BORR,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_ASSIN_CLI,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_STATUS_ENVIO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_SEQ_IDENTIFICADOR,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_TIPO_COLETA,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_HORA_COLETA,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_COLETADO_POR,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_DATA_ENVIO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_HORA_ENVIO,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_PDF,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_COD_CRECAP,'') , \n" +
                "       ISNULL(COLETA.COL_CABEC_DOC_CLI,'') , \n" +
                "       ISNULL(SA3.A3_NOME,'') , \n" +
                "       ISNULL(SA3.A3_COD,'') , \n" +
                "       ISNULL(M0_FILIAL,'') , \n" +
                "       ISNULL(COL_CABEC_DT_CHEGADA,''), \n" +
                "       ISNULL(COLETA.COL_CABEC_ORCAMENTO,'') , \n" +
                "       CASE WHEN SC5.C5_NUM IS NULL THEN ISNULL(SC5A.C5_NUM,'') ELSE ISNULL(SC5.C5_NUM,'') END AS PEDIDO, \n" +
                "       CASE WHEN SC5.C5_NUM IS NULL THEN ISNULL(SC5A.C5_NOTA,'') + '/' + ISNULL(LTRIM(RTRIM(SC5A.C5_SERIE)),'') ELSE ISNULL(SC5.C5_NOTA,'') + '/' + ISNULL(LTRIM(RTRIM(SC5.C5_SERIE)),'') END AS VALE, \n" +
                "       CASE WHEN SC5.C5_NUM IS NOT NULL THEN ISNULL(LTRIM(RTRIM(SF2.F2_NFELETR)),'') /*+ '/' + ISNULL(LTRIM(RTRIM(SF2.F2_CODNFE)),'')*/ ELSE '' END AS NOTA, \n" +
                "       ISNULL(US.NOME,'') \n" +
                "FROM COLETA_CABEC COLETA WITH(NOLOCK) \n" +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SA3010 SA3 WITH(NOLOCK) ON SA3.A3_COD = COLETA.COL_CABEC_VENDEDOR \n" +
                "                                                       AND SA3.D_E_L_E_T_ = '' \n" +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SYS_COMPANY WITH(NOLOCK) ON COLETA.COL_CABEC_FILIAL = M0_CODFIL \n" +
                "                                                        AND SYS_COMPANY.D_E_L_E_T_ = '' \n";
//        if (codVend.equals("000") && !GettersSetters.getTipoUsuario().equals("V")) {
//            query += "JOIN bd_recapador.dbo.USUARIOS US \n" +
//                    " ON (COLETA.COL_CABEC_COD_CRECAP = US.ID OR COLETA.COL_CABEC_VENDEDOR = US.ID) ";
//        }
        queryGeral += " LEFT JOIN bd_recapador.dbo.USUARIOS US WITH(NOLOCK) ON (COLETA.COL_CABEC_VENDEDOR = US.ID \n" +
                "                                                       OR COLETA.COL_CABEC_COD_CRECAP = US.ID) \n" +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.DBO.SC5010 SC5 WITH (NOLOCK) ON COL_CABEC_IDENTIF = SC5.C5_ZIDENT \n" +
                "                                                    AND COL_CABEC_FILIAL = SC5.C5_FILIAL \n" +
                "                                                    AND COL_CABEC_COD_CLI = SC5.C5_CLIENT \n" +
                "                                                    AND COL_CABEC_LOJA_CLI = SC5.C5_LOJACLI \n" +
                "                                                    AND SC5.D_E_L_E_T_ = '' \n" +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SC5010 SC5A WITH (NOLOCK) ON COL_CABEC_IDENTIF = SC5A.C5_ZIDENT \n" +
                "                                                    AND COL_CABEC_FILIAL = SC5A.C5_FILIAL \n" +
                "                                                    AND COL_CABEC_COD_CLI = SC5A.C5_CLIENT \n" +
                "                                                    AND COL_CABEC_LOJA_CLI = SC5A.C5_LOJACLI \n" +
                "                                                    AND SC5A.D_E_L_E_T_ = '' \n" +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.DBO.SF2010 SF2 WITH (NOLOCK) ON SF2.F2_FILIAL = SC5.C5_FILIAL \n" +
                "                                                                  AND SF2.F2_CLIENTE = SC5.C5_CLIENT \n" +
                "                                                                  AND SF2.F2_LOJA = SC5.C5_LOJACLI \n" +
                "                                                                  AND SF2.F2_DOC = SC5.C5_NOTA \n" +
                "                                                                  AND SF2.F2_SERIE = SC5.C5_SERIE \n" +
                "                                                                  AND SF2.D_E_L_E_T_ = '' " +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SF2010 SF2A WITH (NOLOCK) ON SF2A.F2_FILIAL = SC5A.C5_FILIAL \n" +
                "                                                         AND SF2A.F2_CLIENTE = SC5A.C5_CLIENT \n" +
                "                                                         AND SF2A.F2_LOJA = SC5A.C5_LOJACLI \n" +
                "                                                         AND SF2A.F2_DOC = SC5A.C5_NOTA \n" +
                "                                                         AND SF2A.F2_SERIE = SC5A.C5_SERIE \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t AND SF2A.D_E_L_E_T_ = '' \n";
        queryGeral += " WHERE 1 = 1 \n";
        if (!dataColeta.equals("")) {
            if (dataColeta.contains("/")) { //TRATA QUANDO DATA FOR DIGITADA EM CAMPOS
                dataColeta = GettersSetters.converteData(dataColeta, "BR");
            }
            queryGeral += " AND COLETA.COL_CABEC_DATA = '" + dataColeta + "' \n";
        }
        if (!numeroColeta.equals("")) {
            queryGeral += " AND COLETA.COL_CABEC_IDENTIF LIKE '%" + numeroColeta + "%' \n";
        }
        if (!nomeCliColeta.equals("")) {
            queryGeral += " AND COLETA.COL_CABEC_RZ_SOCIAL LIKE '%" + nomeCliColeta + "%' \n";
        }
        if (!codFilial.equals("")) {
            queryGeral += " AND COLETA.COL_CABEC_FILIAL = '" + codFilial + "' \n";
        }
        if (!codVend.equals("") && !codVend.equals("000")) {
            queryGeral += " AND (COLETA.COL_CABEC_COD_CRECAP = '" + codVend + "' OR COLETA.COL_CABEC_VENDEDOR = '" + codVend + "') \n";
        }
        if (codVend.equals("000")) {
            if (GettersSetters.getTipoUsuario().equals("G")) {
                queryGeral += "AND US.GERENTE = '" + GettersSetters.getIdUsuarioLogado() + "' \n";
            } else if (GettersSetters.getTipoUsuario().equals("P") || GettersSetters.getTipoUsuario().equals("T")) {
                queryGeral += "AND US.ID_PERMITIDO LIKE '%" + GettersSetters.getIdUsuarioLogado() + "%' \n";
            }
        }
        if (!status.equals("")) {
            queryGeral += " AND COLETA.COL_CABEC_STATUS_ENVIO IN ('" + status.replace(",", "','") + "') \n";
            if (opcaoSelecionadaBusca.equals("9")) {
                queryGeral += " AND (LTRIM(RTRIM(SC5A.C5_NOTA)) <> '' OR LTRIM(RTRIM(SC5.C5_NOTA)) <> '') \n";
            } else if (opcaoSelecionadaBusca.equals("6")) {
                queryGeral += " AND (LTRIM(RTRIM(SC5A.C5_NOTA)) = '' OR LTRIM(RTRIM(SC5.C5_NOTA)) = '') \n";
            }
        }
        queryGeral += "GROUP BY \n" +
                "            COLETA.COL_CABEC_FILIAL, \n" +
                "            COLETA.COL_CABEC_IDENTIF, \n" +
                "            COLETA.COL_CABEC_DATA, \n" +
                "            COLETA.COL_CABEC_COD_CLI, \n" +
                "            COLETA.COL_CABEC_LOJA_CLI, \n" +
                "            COLETA.COL_CABEC_RZ_SOCIAL, \n" +
                "            COLETA.COL_CABEC_VENDEDOR, \n" +
                "            COLETA.COL_CABEC_QNT_ITENS, \n" +
                "            COLETA.COL_CABEC_TOTAL, \n" +
                "            COLETA.COL_CABEC_COND_PG, \n" +
                "            COLETA.COL_CABEC_FORMA_PG, \n" +
                "            COLETA.COL_CABEC_INFO_ADIC, \n" +
                "            COLETA.COL_CABEC_BORRACHEIRO, \n" +
                "            COLETA.COL_CABEC_NOME_BORRACHEIRO, \n" +
                "            COLETA.COL_CABEC_DOC_BORRACHEIRO, \n" +
                "            COLETA.COL_CABEC_ASSIN_BORR, \n" +
                "            COLETA.COL_CABEC_ASSIN_CLI, \n" +
                "            COLETA.COL_CABEC_STATUS_ENVIO, \n" +
                "            COLETA.COL_CABEC_SEQ_IDENTIFICADOR, \n" +
                "            COLETA.COL_CABEC_TIPO_COLETA, \n" +
                "            COLETA.COL_CABEC_HORA_COLETA, \n" +
                "            COLETA.COL_CABEC_COLETADO_POR, \n" +
                "            COLETA.COL_CABEC_DATA_ENVIO, \n" +
                "            COLETA.COL_CABEC_HORA_ENVIO, \n" +
                "            COLETA.COL_CABEC_PDF, \n" +
                "            COLETA.COL_CABEC_COD_CRECAP, \n" +
                "            COLETA.COL_CABEC_DOC_CLI, \n" +
                "            SA3.A3_NOME, \n" +
                "            SA3.A3_COD, \n" +
                "            M0_FILIAL, \n" +
                "            COL_CABEC_DT_CHEGADA, \n" +
                "            COLETA.COL_CABEC_ORCAMENTO, \n" +
                "            SC5.C5_NUM, \n" +
                "            SC5A.C5_NUM, \n" +
                "            SC5.C5_NOTA, \n" +
                "            SC5A.C5_NOTA, \n" +
                "            SC5.C5_SERIE, \n" +
                "            SC5A.C5_SERIE, \n" +
                "            SF2.F2_NFELETR, \n" +
                "            SF2.F2_CODNFE,\n" +
                "            US.NOME \n" +
                "ORDER BY COLETA.COL_CABEC_FILIAL ASC, COLETA.COL_CABEC_IDENTIF ASC, COLETA.COL_CABEC_DATA ASC ";

//        System.out.println(query);

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return preparedStatement.executeQuery();
        } catch (SQLException expt) {
            expt.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(expt.getMessage());
            return null;
        }
    }

    public ResultSet buscaColetaExtItens(String filial, String identificador, String data, String codCli, String lojaCli) {
        queryGeral = "SELECT\n" +
                "  COL_IT_FILIAL,\n" +
                "  COL_IT_IDENTIF,\n" +
                "  COL_IT_ITEM,\n" +
                "  COL_IT_DATA,\n" +
                "  COL_IT_COD_CLI,\n" +
                "  COL_IT_LOJA_CLI,\n" +
                "  COL_IT_QTD,\n" +
                "  COL_IT_VALOR_UNIT,\n" +
                "  COL_IT_VALOR,\n" +
                "  COL_IT_COD_PROD,\n" + //10
                "  COL_IT_BITOLA,\n" +
                "  COL_IT_MARCA,\n" +
                "  COL_IT_MODELO,\n" +
                "  COL_IT_SERIE,\n" +
                "  COL_IT_DOT,\n" +
                "  COL_IT_MONTADO,\n" +
                "  COL_IT_DESENHO,\n" +
                "  COL_IT_URGENTE,\n" +
                "  COL_IT_PERC_COM_BORR,\n" +
                "  COL_IT_VLR_COM_BORR,\n" + //20
                "  COL_IT_STATUS_ENVIO,\n" +
                "  COL_IT_OBS,\n" + //22
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_NUM,'') = '' THEN ISNULL(SC6A.C6_NUM, '')\n" +
                "    ELSE ISNULL(SC6.C6_NUM, '')\n" +
                "  END AS PEDIDO,\n" + //23
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_ITEM,'') = '' THEN ISNULL(SC6A.C6_ITEM, '')\n" +
                "    ELSE ISNULL(SC6.C6_ITEM, '')\n" +
                "  END AS ITEM_PV,\n" + //24
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_NUMOP,'') = '' THEN ISNULL(SC6A.C6_NUMOP, '')\n" +
                "    ELSE ISNULL(SC6.C6_NUMOP, '')\n" +
                "  END AS NUMERO_OP,\n" + //25
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_ITEMOP,'') = '' THEN ISNULL(SC6A.C6_ITEMOP, '')\n" +
                "    ELSE ISNULL(SC6.C6_ITEMOP, '')\n" +
                "  END AS ITEM_OP,\n" + //26
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_ZRECUS,'') = '' THEN ISNULL(SC6A.C6_ZRECUS, '')\n" +
                "    ELSE ISNULL(SC6.C6_ZRECUS, '')\n" +
                "  END AS RECUSADO_PV,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.STATUS,'') = '' THEN ISNULL(RPA.STATUS, '')\n" +
                "    ELSE ISNULL(RP.STATUS, '')\n" +
                "  END AS STATUS_FABRICA,\n" + //28
                "  ISNULL(EP.DATA_ENTRADA, '') DATA_ENTRADA,\n" + //29
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_EXAME_INICIAL,'') = '' THEN ISNULL(RPA.DATA_EXAME_INICIAL, '')\n" +
                "    ELSE ISNULL(RP.DATA_EXAME_INICIAL, '')\n" +
                "  END AS EXAME_INICIAL,\n" + //30
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_RASPAGEM,'') = '' THEN ISNULL(RPA.DATA_RASPAGEM, '')\n" +
                "    ELSE ISNULL(RP.DATA_RASPAGEM, '')\n" +
                "  END AS RASPAGEM,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_ESCAREACAO,'') = '' THEN ISNULL(RPA.DATA_ESCAREACAO, '')\n" +
                "    ELSE ISNULL(RP.DATA_ESCAREACAO, '')\n" +
                "  END AS ESCAREACAO,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_CONSERTO,'') = '' THEN ISNULL(RPA.DATA_CONSERTO, '')\n" +
                "    ELSE ISNULL(RP.DATA_CONSERTO, '')\n" +
                "  END AS APLIC_CONSERTOS,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_CORTE,'') = '' THEN ISNULL(RPA.DATA_CORTE, '')\n" +
                "    ELSE ISNULL(RP.DATA_CORTE, '')\n" +
                "  END AS CORTE_BANDA_CAMELBACK,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_FIM,'') = '' THEN ISNULL(RPA.DATA_FIM, '')\n" +
                "    ELSE ISNULL(RP.DATA_FIM, '')\n" +
                "  END AS APLIC_BANDA_CAMELBACK,\n" + //35
                "  CASE\n" +
                "    WHEN ISNULL(CAPM.DATA,'') = '' THEN ISNULL(CAPMA.DATA, '')\n" +
                "    ELSE ISNULL(CAPM.DATA, '')\n" +
                "  END AS AUTOCLAVE,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(RP.DATA_EXAME_FINAL,'') = '' THEN ISNULL(RPA.DATA_EXAME_FINAL, '')\n" +
                "    ELSE ISNULL(RP.DATA_EXAME_FINAL, '')\n" +
                "  END AS DATA_EXAME_FINAL,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_NOTA,'') = '' THEN ISNULL(SC6A.C6_NOTA, '')\n" +
                "    ELSE ISNULL(SC6.C6_NOTA, '')\n" +
                "  END AS NOTA,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(SC6.C6_SERIE,'') = '' THEN ISNULL(SC6A.C6_SERIE, '')\n" +
                "    ELSE ISNULL(SC6.C6_SERIE, '')\n" +
                "  END AS SERIE,\n" +
                "  CASE\n" +
                "    WHEN ISNULL(EXPD.DATA_EXPEDICAO,'') = '' THEN ISNULL(EXPDA.DATA_EXPEDICAO, '')\n" +
                "    ELSE ISNULL(EXPD.DATA_EXPEDICAO, '')\n" +
                "  END AS EXPEDICAO,\n" + //40
                "  CASE\n" +
                "   WHEN ISNULL(SETRETR.SETOR,'') = '' THEN ISNULL(SETRETRA.SETOR, '')\n" +
                "   ELSE ISNULL(SETRETR.SETOR, '')\n" +
                "  END AS RETRABALHO,\n" +
                "  CASE\n" +
                "   WHEN ISNULL(RP.SETOR_RECUSA,'') = '' THEN ISNULL(RPA.SETOR_RECUSA, '')\n" +
                "   ELSE ISNULL(RP.SETOR_RECUSA, '')\n" +
                "  END AS SETOR_RECUSA, \n" + //42
                "  CASE\n" +
                "   WHEN ISNULL(RP.MOTIVO_RECUSA,'') = '' THEN ISNULL((SELECT DESCRICAO FROM bd_fabrica.dbo.MOTIVOS_RECUSA WHERE cast(ID as nvarchar) = RPA.MOTIVO_RECUSA), ISNULL(RPA.MOTIVO_RECUSA,''))\n" +
                "   ELSE ISNULL((SELECT DESCRICAO FROM bd_fabrica.dbo.MOTIVOS_RECUSA WHERE cast(ID as nvarchar) = RP.MOTIVO_RECUSA), ISNULL(RP.MOTIVO_RECUSA,''))\n" +
                "  END AS MOTIVO_RECUSA, \n" + //43
                "  CASE\n" +
                "    WHEN ISNULL(EXPD.HORA_EXPEDICAO,'') = '' THEN ISNULL(EXPDA.HORA_EXPEDICAO, '')\n" +
                "    ELSE ISNULL(EXPD.HORA_EXPEDICAO, '')\n" +
                "  END AS HORA_EXPEDICAO \n" + //44
                "FROM COLETA_ITENS WITH (NOLOCK)\n" +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.DBO.SC6010 SC6 WITH (NOLOCK)\n" +
                "  ON COL_IT_FILIAL = SC6.C6_FILIAL\n" +
                "  AND COL_IT_IDENTIF = SC6.C6_ZIDENT\n" +
                "  AND COL_IT_COD_CLI = SC6.C6_CLI\n" +
                "  AND COL_IT_LOJA_CLI = SC6.C6_LOJA\n" +
                "  AND REPLICATE('0', 2 - LEN(COL_IT_ITEM)) + RTRIM(COL_IT_ITEM) = SC6.C6_ITEM\n" +
                "  AND SC6.D_E_L_E_T_ = ''\n" +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SC6010 SC6A WITH (NOLOCK)\n" +
                "  ON COL_IT_FILIAL = SC6A.C6_FILIAL\n" +
                "  AND COL_IT_IDENTIF = SC6A.C6_ZIDENT\n" +
                "  AND COL_IT_COD_CLI = SC6A.C6_CLI\n" +
                "  AND COL_IT_LOJA_CLI = SC6A.C6_LOJA\n" +
                "  AND REPLICATE('0', 2 - LEN(COL_IT_ITEM)) + RTRIM(COL_IT_ITEM) = SC6A.C6_ITEM\n" +
                "  AND SC6A.D_E_L_E_T_ = ''\n" +
                "LEFT JOIN bd_fabrica.dbo.ENTRADA_PNEUS EP WITH (NOLOCK)\n" +
                "  ON EP.FILIAL = COL_IT_FILIAL\n" +
                "  AND EP.COLETA = COL_IT_IDENTIF\n" +
                "  AND EP.ITEM = COL_IT_ITEM\n" +
                "LEFT JOIN bd_fabrica.dbo.RASPA_CORTE RP WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = RP.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = RP.FILIAL\n" +
                "  AND SC6.C6_NUMOP + SC6.C6_ITEMOP = SUBSTRING(RP.FICHA, 1, 8)\n" +
                "LEFT JOIN bd_fabrica.dbo.RASPA_CORTE RPA WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = RPA.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = RPA.FILIAL\n" +
                "  AND SC6A.C6_NUMOP + SC6A.C6_ITEMOP = SUBSTRING(RPA.FICHA, 1, 8)\n" +
                "LEFT JOIN bd_fabrica.dbo.CONTROLE_AUTOCLAVE_PM CAPM WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = CAPM.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = CAPM.FILIAL\n" +
                "  AND SC6.C6_NUMOP + SC6.C6_ITEMOP = SUBSTRING(CAPM.FICHA, 1, 8)\n" +
                "LEFT JOIN bd_fabrica.dbo.CONTROLE_AUTOCLAVE_PM CAPMA WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = CAPMA.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = CAPMA.FILIAL\n" +
                "  AND SC6A.C6_NUMOP + SC6A.C6_ITEMOP = SUBSTRING(CAPMA.FICHA, 1, 8)\n" +
                "LEFT JOIN [SERVER\\SERVER].Protheus11.DBO.SD2010 SD2 WITH (NOLOCK)\n" +
                " ON SD2.D2_FILIAL = SC6.C6_FILIAL\n" +
                " AND SD2.D2_CLIENTE = SC6.C6_CLI\n" +
                " AND SD2.D2_LOJA = SC6.C6_LOJA\n" +
                " AND SD2.D2_PEDIDO = SC6.C6_NUM\n" +
                " AND SD2.D2_ITEMPV = SC6.C6_ITEM\n" +
                " AND SD2.D_E_L_E_T_ = ''\n" +
                "LEFT JOIN PROTHEUS11_Agenda.DBO.SD2010 SD2A WITH (NOLOCK)\n" +
                " ON SD2A.D2_FILIAL = SC6A.C6_FILIAL\n" +
                " AND SD2A.D2_CLIENTE = SC6A.C6_CLI\n" +
                " AND SD2A.D2_LOJA = SC6A.C6_LOJA\n" +
                " AND SD2A.D2_PEDIDO = SC6A.C6_NUM\n" +
                " AND SD2A.D2_ITEMPV = SC6A.C6_ITEM\n" +
                " AND SD2A.D_E_L_E_T_ = ''\n" +
                "LEFT JOIN bd_fabrica.dbo.EXPEDICAO EXPD WITH (NOLOCK)\n" +
                " ON SC6.C6_FILIAL = EXPD.FILIAL\n" +
                " AND SC6.C6_CLI = EXPD.COD_CLI\n" +
                " AND SC6.C6_LOJA = EXPD.LOJA_CLI\n" +
                " AND SC6.C6_NUMOP + SC6.C6_ITEMOP = SUBSTRING(EXPD.FICHA, 1, 8)\n" +
                " AND REPLICATE('0', 2 - LEN(COL_IT_ITEM)) + RTRIM(COL_IT_ITEM) = SC6.C6_ITEM\n" +
//                "  ON SD2.D2_FILIAL = EXPD.FILIAL\n" +
//                "  AND SD2.D2_CLIENTE = EXPD.COD_CLI\n" +
//                "  AND SD2.D2_LOJA = EXPD.LOJA_CLI\n" +
//                "  AND SD2.D2_DOC = EXPD.DOC\n" +
//                "  AND SD2.D2_SERIE = EXPD.SERIE\n" +
//                "  AND SD2.D2_ITEM = EXPD.ITEM\n" +
                "LEFT JOIN bd_fabrica.dbo.EXPEDICAO EXPDA WITH (NOLOCK)\n" +
                " ON SC6A.C6_FILIAL = EXPDA.FILIAL\n" +
                " AND SC6A.C6_CLI = EXPDA.COD_CLI\n" +
                " AND SC6A.C6_LOJA = EXPDA.LOJA_CLI\n" +
                " AND SC6A.C6_NUMOP + SC6A.C6_ITEMOP = SUBSTRING(EXPDA.FICHA, 1, 8)\n" +
                " AND REPLICATE('0', 2 - LEN(COL_IT_ITEM)) + RTRIM(COL_IT_ITEM) = SC6A.C6_ITEM\n" +
//                "  ON SD2A.D2_FILIAL = EXPDA.FILIAL\n" +
//                "  AND SD2A.D2_CLIENTE = EXPDA.COD_CLI\n" +
//                "  AND SD2A.D2_LOJA = EXPDA.LOJA_CLI\n" +
//                "  AND SD2A.D2_DOC = EXPDA.DOC\n" +
//                "  AND SD2A.D2_SERIE = EXPDA.SERIE\n" +
//                "  AND SD2A.D2_ITEM = EXPDA.ITEM\n" +
                "LEFT JOIN bd_fabrica.dbo.RETRABALHO RETR WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = RETR.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = RETR.FILIAL\n" +
                "  AND SC6.C6_NUMOP + SC6.C6_ITEMOP = SUBSTRING(RETR.FICHA, 1, 8)\n" +
                "LEFT JOIN bd_fabrica.dbo.RETRABALHO RETRA WITH (NOLOCK)\n" +
                "  ON COL_IT_IDENTIF = RETRA.NUMERO_COLETA\n" +
                "  AND COL_IT_FILIAL = RETRA.FILIAL\n" +
                "  AND SC6A.C6_NUMOP + SC6A.C6_ITEMOP = SUBSTRING(RETRA.FICHA, 1, 8)\n" +
                "LEFT JOIN bd_fabrica.dbo.SETORES_RETRABALHO SETRETR WITH (NOLOCK)\n" +
                "  ON RETR.STATUS_RETRABALHO = SETRETR.ID\n" +
                "  AND SETRETR.STATUS = 'A'\n" +
                "LEFT JOIN bd_fabrica.dbo.SETORES_RETRABALHO SETRETRA WITH (NOLOCK)\n" +
                "  ON RETRA.STATUS_RETRABALHO = SETRETRA.ID\n" +
                "  AND SETRETRA.STATUS = 'A'\n" +
                "WHERE 1 = 1\n" +
                "  AND COL_IT_FILIAL = '" + filial + "' \n" +
                "  AND COL_IT_IDENTIF = '" + identificador + "' \n";
        if (!data.equals("")) {
            queryGeral += "  AND COL_IT_DATA = '" + GettersSetters.converteData(data, "BR") + "' \n" +
                    "  AND COL_IT_COD_CLI = '" + codCli + "' \n" +
                    "  AND COL_IT_LOJA_CLI = '" + lojaCli + "' \n";
        }
        queryGeral += "GROUP BY COL_IT_FILIAL,\n" +
                "         COL_IT_IDENTIF,\n" +
                "         COL_IT_ITEM,\n" +
                "         COL_IT_DATA,\n" +
                "         COL_IT_COD_CLI,\n" +
                "         COL_IT_LOJA_CLI,\n" +
                "         COL_IT_QTD,\n" +
                "         COL_IT_VALOR_UNIT,\n" +
                "         COL_IT_VALOR,\n" +
                "         COL_IT_COD_PROD,\n" +
                "         COL_IT_BITOLA,\n" +
                "         COL_IT_MARCA,\n" +
                "         COL_IT_MODELO,\n" +
                "         COL_IT_SERIE,\n" +
                "         COL_IT_DOT,\n" +
                "         COL_IT_MONTADO,\n" +
                "         COL_IT_DESENHO,\n" +
                "         COL_IT_URGENTE,\n" +
                "         COL_IT_PERC_COM_BORR,\n" +
                "         COL_IT_VLR_COM_BORR,\n" +
                "         COL_IT_STATUS_ENVIO,\n" +
                "         COL_IT_OBS,\n" +
                "         SC6.C6_NUM,\n" +
                "         SC6A.C6_NUM,\n" +
                "         SC6.C6_ITEM,\n" +
                "         SC6A.C6_ITEM,\n" +
                "         SC6.C6_NUMOP,\n" +
                "         SC6.C6_ITEMOP,\n" +
                "         SC6A.C6_NUMOP,\n" +
                "         SC6A.C6_ITEMOP,\n" +
                "         RP.DATA_EXAME_INICIAL,\n" +
                "         RPA.DATA_EXAME_INICIAL,\n" +
                "         CAPM.DATA,\n" +
                "         CAPMA.DATA,\n" +
                "         RP.DATA_RASPAGEM,\n" +
                "         RPA.DATA_RASPAGEM,\n" +
                "         RP.DATA_ESCAREACAO,\n" +
                "         RPA.DATA_ESCAREACAO,\n" +
                "         RP.DATA_CONSERTO,\n" +
                "         RPA.DATA_CONSERTO,\n" +
                "         RP.DATA_CORTE,\n" +
                "         RPA.DATA_CORTE,\n" +
                "         RP.DATA_FIM,\n" +
                "         RPA.DATA_FIM,\n" +
                "         RP.DATA_EXAME_FINAL,\n" +
                "         RPA.DATA_EXAME_FINAL,\n" +
                "         RP.DATA_ENVIO_CORTE,\n" +
                "         RPA.DATA_ENVIO_CORTE,\n" +
                "         RPA.STATUS,\n" +
                "         RP.STATUS,\n" +
                "         RP.DATA_ENVIO_RASPAGEM,\n" +
                "         RPA.DATA_ENVIO_RASPAGEM,\n" +
                "         SC6.C6_ZRECUS,\n" +
                "         SC6A.C6_ZRECUS,\n" +
                "         EP.DATA_ENTRADA,\n" +
                "         SC6.C6_NOTA,\n" +
                "         SC6A.C6_NOTA,\n" +
                "         SC6.C6_SERIE,\n" +
                "         SC6A.C6_SERIE,\n" +
                "         EXPD.DATA_EXPEDICAO,\n" +
                "         EXPDA.DATA_EXPEDICAO,\n" +
                "		  RETR.DATA_RETRABALHO,\n" +
                "         RETRA.DATA_RETRABALHO,\n" +
                "         SETRETR.SETOR, \n " +
                "         SETRETRA.SETOR, \n " +
                "         RP.SETOR_RECUSA, \n " +
                "         RPA.SETOR_RECUSA, \n" +
                "         RP.MOTIVO_RECUSA,\n" +
                "         RPA.MOTIVO_RECUSA, \n" +
                "         EXPD.HORA_EXPEDICAO, \n" +
                "         EXPDA.HORA_EXPEDICAO \n" +
                "ORDER BY CAST(COL_IT_ITEM AS numeric)";

//        System.out.println(query);

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return preparedStatement.executeQuery();
        } catch (SQLException exp) {
            exp.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(exp.getMessage());
            return null;
        }
    }

//    public byte[] buscaPdfColeta() {
////        String qry = "SELECT COL_CABEC_PDF " +
////                "FROM COLETA_CABEC " +
////                "WHERE COL_CABEC_IDENTIF = '" + identificador + "'";
//
//        String qry = "SELECT COL_CABEC_ASSIN_CLI FROM COLETA_CABEC " +
//                "WHERE COL_CABEC_IDENTIF IN ('016230') ";
//
//        //System.out.println(qry);
//
//        byte[] img = null;
//
//        try {
//            resultSet = stmt.executeQuery(qry);
//
//            while (resultSet.next()) {
//                img = resultSet.getBytes(1);
//            }
//
//        } catch (SQLException exp) {
//            exp.printStackTrace();
//        }
//
//        return img;
//    }

    public void selecionaDadosFilial(String filial) {
        descricaoFilial = "";
        cnpjFilial = "";
        enderecoFilial = "";
        cidadeFilial = "";
        telefoneFilial = "";
        cepFilial = "";

        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            queryGeral = "SELECT DISTINCT M0_CODFIL, LTRIM(RTRIM(M0_NOMECOM)) NOME, REPLACE(M0_TEL,' ',''), LTRIM(RTRIM(M0_CGC)), " +
                    "       LTRIM(RTRIM(M0_ENDENT)) + ' - ' + " +
                    "       CASE LTRIM(RTRIM(M0_COMPENT)) WHEN '' THEN '' ELSE LTRIM(RTRIM(M0_COMPENT)) + ' - ' END + LTRIM(RTRIM(M0_BAIRENT)) ENDERECO, " +
                    "       M0_CEPENT, LTRIM(RTRIM(M0_CIDENT)) + '/' + M0_ESTENT MUNICIPIO " +
                    "FROM SYS_COMPANY WITH(NOLOCK) " +
                    "WHERE M0_NOMECOM <> '' " +
                    "  AND M0_CODFIL = '" + filial + "' " +
                    "ORDER BY 1";

            //System.out.println(SQLSelecionaDadosFilial);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                descricaoFilial = resultSet.getString(2);
                telefoneFilial = resultSet.getString(3);
                cnpjFilial = resultSet.getString(4);
                enderecoFilial = resultSet.getString(5);
                cepFilial = resultSet.getString(6);
                cidadeFilial = resultSet.getString(7);
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException expt) {
            expt.printStackTrace();
        }
    }

    public String selecionaMaxNumColeta(String codVend) {
        String numeroColetaRet = "1";

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT " +
                    "       COALESCE(MAX(COL_CABEC_SEQ_IDENTIFICADOR) + 1, '1') NUMERO" +
                    " FROM COLETA_CABEC WITH(NOLOCK) " +
                    " WHERE 1 = 1 " +
                    "  AND (COL_CABEC_COLETADO_POR = '" + codVend + "' OR COL_CABEC_COD_CRECAP = '" + codVend + "')";

//            System.out.println(query);

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                numeroColetaRet = resultSet.getString(1);
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numeroColetaRet;
    }

    public void selectComissaoBorracheiros() {
        arrIdComissBorracheiro.clear();
        arrFxIniComissBorracheiro.clear();
        arrFxFimComissBorracheiro.clear();
        arrPercComissBorracheiro.clear();

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT * " +
                    "FROM COLETA_COMISS_BORR";

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                arrIdComissBorracheiro.add(resultSet.getString(1));
                arrFxIniComissBorracheiro.add(resultSet.getString(2));
                arrFxFimComissBorracheiro.add(resultSet.getString(3));
                arrPercComissBorracheiro.add(resultSet.getString(4));
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException expt) {
            expt.printStackTrace();
        }
    }

    public String selecionaParametro(String parametro) {
        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            queryGeral = "SELECT X6_CONTEUD " +
                    "FROM SX6010 WITH(NOLOCK) " +
                    "WHERE X6_VAR = '" + parametro + "' " +
                    "AND X6_FIL = ''";

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                parametro = resultSet.getString(1).trim();
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException expt) {
            expt.printStackTrace();
        }
        return parametro;
    }

    public String selecionaParametroAtualizaDados(String parametro) {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "SELECT COL_CONTEUDO " +
                    "FROM COLETA_PARAMETROS WITH(NOLOCK) " +
                    "WHERE COL_PARAMETRO = '" + parametro + "' ";

            preparedStatement = connection.prepareStatement(queryGeral);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                parametro = resultSet.getString(1).trim();
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException expt) {
            expt.printStackTrace();
        }
        return parametro;
    }

    public ResultSet buscaConfigEmail(String filial) {
        try {
            ConnectToDatabase("P", GettersSetters.getConexaoBD());

            queryGeral = "SELECT\n" +
                    "  MV_SMTPAAC CONTA,\n" +
                    "  MV_SMTPFAC CONTA2,\n" +
                    "  MV_SMTPAUT AUTENTICA,\n" +
                    "  MV_SMTPFPS SENHA,\n" +
                    "  MV_SMTPSRV SERVIDOR,\n" +
                    "  MV_SMTPSSL SSL,\n" +
                    "  MV_SMTPTLS TLS\n" +
                    "FROM (SELECT\n" +
                    "  LTRIM(RTRIM(SPD00.PARAMETRO)) PARAMETRO,\n" +
                    "  LTRIM(RTRIM(SPD00.CONTEUDO)) CONTEUDO\n" +
                    "FROM SYS_COMPANY M0 (NOLOCK)\n" +
                    "LEFT JOIN Sped12.dbo.SPED001 SPD01 (NOLOCK)\n" +
                    "  ON SPD01.CNPJ = M0.M0_CGC\n" +
                    "LEFT JOIN Sped12.dbo.SPED000 SPD00 (NOLOCK)\n" +
                    "  ON SPD01.ID_ENT = SPD00.ID_ENT\n" +
                    "WHERE 1 = 1\n" +
                    "AND M0_CODFIL = '" + filial + "'\n" +
                    "AND SPD00.PARAMETRO IN ('MV_SMTPAAC', 'MV_SMTPFAC', 'MV_SMTPAUT', 'MV_SMTPFPS', 'MV_SMTPSRV', 'MV_SMTPSSL', 'MV_SMTPTLS')\n" +
                    "AND SPD00.D_E_L_E_T_ = ''\n" +
                    "AND SPD01.D_E_L_E_T_ = ''\n" +
                    "AND M0.D_E_L_E_T_ = ''\n" +
                    "GROUP BY M0.M0_FILIAL,\n" +
                    "         SPD01.ID_ENT,\n" +
                    "         SPD00.PARAMETRO,\n" +
                    "         SPD00.CONTEUDO) TAB1\n" +
                    "PIVOT (MAX(CONTEUDO) FOR PARAMETRO IN (MV_SMTPAAC, MV_SMTPFAC, MV_SMTPAUT, MV_SMTPFPS, MV_SMTPSRV, MV_SMTPSSL, MV_SMTPTLS)) AS PVTBL";

            preparedStatement = connection.prepareStatement(queryGeral, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt("" + e);
            return null;
        }
    }

    /*
     * ---------- DELETES -------------
     */
    public boolean deleteColeta(String filial, String data, String numeroColeta, boolean reenvio) {
        int rows = 0;
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            queryGeral = "DELETE FROM COLETA_CABEC " +
                    "WHERE COL_CABEC_FILIAL = '" + filial + "' " +
                    "  AND COL_CABEC_IDENTIF = '" + numeroColeta + "' " +
                    "  AND COL_CABEC_DATA = '" + data + "'";
            if (!reenvio) {
                queryGeral += "  AND COL_CABEC_STATUS_ENVIO = '2'";
            } else {
                queryGeral += "  AND COL_CABEC_STATUS_ENVIO = '99'";
            }

            preparedStatement = connection.prepareStatement(queryGeral);
            rows = preparedStatement.executeUpdate();

            queryGeral = "DELETE FROM COLETA_ITENS " +
                    "WHERE COL_IT_FILIAL = '" + filial + "' " +
                    "  AND COL_IT_IDENTIF = '" + numeroColeta + "' " +
                    "  AND COL_IT_DATA = '" + data + "' ";
            if (!reenvio) {
                queryGeral += "  AND COL_IT_STATUS_ENVIO = '2'";
            } else {
                queryGeral += "  AND COL_IT_STATUS_ENVIO = '99'";
            }
            preparedStatement = connection.prepareStatement(queryGeral);
            rows += preparedStatement.executeUpdate();

            queryGeral = "DELETE FROM COLETA_CLI " +
                    "WHERE COL_CLI_FILIAL = '" + filial + "' " +
                    "  AND COL_CLI_IDENTIF = '" + numeroColeta + "' " +
                    "  AND COL_CLI_DATA = '" + data + "' ";
            if (!reenvio) {
                queryGeral += "  AND COL_CLI_STATUS_ENVIO = '2'";
            } else {
                queryGeral += "  AND COL_CLI_STATUS_ENVIO = '99'";
            }
            preparedStatement = connection.prepareStatement(queryGeral);
            rows += preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            return rows > 0;
        } catch (SQLException expt) {
            expt.printStackTrace();
            return false;
        }
    }

    /*
     * ---------- UPDATES -------------
     */
    public boolean updateFaixasComissaoBorracheiros(String id, String fxIni, String fxFim, String percComiss) {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            queryGeral = "UPDATE COLETA_COMISS_BORR " +
                    " SET COLETA_COMISS_FX_INI = '" + fxIni + "'," +
                    "     COLETA_COMISS_FX_FIM = '" + fxFim + "'," +
                    "     COLETA_COMISS_PERC_COMISS = '" + percComiss + "'" +
                    " WHERE COLETA_COMISS_ID = '" + id + "'";

            preparedStatement = connection.prepareStatement(queryGeral);
            int rows = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return rows > 0;
        } catch (SQLException expt) {
            erroExcecao = expt.toString();
            expt.printStackTrace();
            return false;
        }
    }

    /*
     * INSERTS *
     **/
    public boolean insereDadosColetaCabecalho(String filial, String numero, String data, String codCli, String lojaCli, String nomeCli, String codVend, String qtdItens,
                                              String total, String condPg, String formaPg, String infoAdd, String codBorr, String nomeBorrach, String docBorr,
                                              String assinBorr, String assinCli, int idSeq, String tipoColeta, String hora, String coletadoPor, String pdf,
                                              String idUsr, String docCli, String dataChegada, String orcamento, Context context, String offline, String cliNovo) {

        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat formatDataEn = new SimpleDateFormat("yyyyMMdd");
            Date dataEng = new Date();
            String dataEnvio = formatDataEn.format(dataEng);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
            Date horaEnv = Calendar.getInstance().getTime();
            String horaEnvio = horaFormat.format(horaEnv);

            ConnectToDatabase("C", GettersSetters.getConexaoBD());
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO COLETA_CABEC " +
                    "(COL_CABEC_FILIAL, " +
                    "COL_CABEC_IDENTIF, " +
                    "COL_CABEC_DATA, " +
                    "COL_CABEC_COD_CLI, " +
                    "COL_CABEC_LOJA_CLI, " +
                    "COL_CABEC_RZ_SOCIAL, " +
                    "COL_CABEC_VENDEDOR, " +
                    "COL_CABEC_QNT_ITENS, " +
                    "COL_CABEC_TOTAL, " +
                    "COL_CABEC_COND_PG, " +
                    "COL_CABEC_FORMA_PG, " +
                    "COL_CABEC_INFO_ADIC, " +
                    "COL_CABEC_BORRACHEIRO, " +
                    "COL_CABEC_NOME_BORRACHEIRO, " +
                    "COL_CABEC_DOC_BORRACHEIRO, " +
                    "COL_CABEC_ASSIN_BORR, " +
                    "COL_CABEC_ASSIN_CLI, " +
                    "COL_CABEC_SEQ_IDENTIFICADOR, " +
                    "COL_CABEC_TIPO_COLETA, " +
                    "COL_CABEC_HORA_COLETA, " +
                    "COL_CABEC_COLETADO_POR, " +
                    "COL_CABEC_DATA_ENVIO, " +
                    "COL_CABEC_HORA_ENVIO, " +
                    "COL_CABEC_PDF, " +
                    "COL_CABEC_COD_CRECAP, " +
                    "COL_CABEC_DOC_CLI, " +
                    "COL_CABEC_DT_CHEGADA, " +
                    "COL_CABEC_ORCAMENTO, " +
                    "COL_CABEC_STATUS_ENVIO, " +
                    "COL_CABEC_OFFLINE, " +
                    "COL_CABEC_CLI_NOVO) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            stmt.setString(1, filial);
            stmt.setString(2, numero);
            stmt.setString(3, data);
            stmt.setString(4, codCli);
            stmt.setString(5, lojaCli);
            stmt.setString(6, nomeCli);
            stmt.setString(7, codVend);
            stmt.setString(8, qtdItens);
            stmt.setDouble(9, Double.parseDouble(total));
            stmt.setString(10, condPg);
            stmt.setString(11, formaPg);
            stmt.setString(12, infoAdd);
            stmt.setString(13, codBorr);
            stmt.setString(14, nomeBorrach);
            stmt.setString(15, docBorr);
            stmt.setString(16, assinBorr);
            stmt.setString(17, assinCli);
            stmt.setInt(18, idSeq);
            stmt.setString(19, tipoColeta.replace("'", ""));
            stmt.setString(20, hora);
            stmt.setString(21, coletadoPor);
            stmt.setString(22, dataEnvio);
            stmt.setString(23, horaEnvio);
            stmt.setString(24, pdf);
            stmt.setString(25, idUsr);
            stmt.setString(26, docCli);
            stmt.setString(27, dataChegada);
            stmt.setString(28, orcamento);
            stmt.setString(29, "2"); //2 enviada
            stmt.setString(30, offline);
            stmt.setString(31, cliNovo);

            int rows = stmt.executeUpdate();
            stmt.close();
            connection.close();

            return rows > 0;
        } catch (SQLException expt) {
            deleteColeta(filial, data, numero, false);

            ConexaoBDInt db = new ConexaoBDInt(context);
            db.updColeta("UPDATE COLETA_ITENS " +
                    "SET COL_IT_STATUS_ENVIO = '1' " +
                    "WHERE COL_IT_FILIAL = '" + filial + "'" +
                    "  AND COL_IT_IDENTIF = '" + numero + "'" +
                    "  AND COL_IT_COD_CLI = '" + codCli + "'" +
                    "  AND COL_IT_LOJA_CLI = '" + lojaCli + "'" +
                    "  AND COL_IT_DATA = '" + data + "'");

            /* ATUALIZA O STATUS DA COLETA **/
            db.updColeta("UPDATE COLETA_CABEC " +
                    "SET COL_CABEC_STATUS_ENVIO = '1' " +
                    "WHERE COL_CABEC_FILIAL = '" + filial + "' " +
                    "  AND COL_CABEC_IDENTIF = '" + numero + "'" +
                    "  AND COL_CABEC_COD_CLI = '" + codCli + "'" +
                    "  AND COL_CABEC_LOJA_CLI = '" + lojaCli + "'" +
                    "  AND COL_CABEC_DATA = '" + data + "'");

            expt.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(expt.getMessage());
            Log.v("Coleta Insere Cabec EXT", "", expt.fillInStackTrace());
            return false;
        }
    }

    public boolean insereDadosColetaItens(String filial, String numero, String item, String data, String codCli, String lojaCli, String qtd, String vlrUnit,
                                          String total, String codPrd, String bitola, String marca, String modelo, String serie, String dot,
                                          String montado, String desenho, String urgente, String percComBorr, String vlrComBorr,
                                          String observacao, String cAgua, String cCamara, String garantia, Context context,
                                          String filialOri, String coletaOri, String itemOri) {

        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO COLETA_ITENS " +
                    "(COL_IT_FILIAL , " +
                    "COL_IT_IDENTIF, " +
                    "COL_IT_ITEM, " +
                    "COL_IT_DATA, " +
                    "COL_IT_COD_CLI, " +
                    "COL_IT_LOJA_CLI, " +
                    "COL_IT_QTD, " +
                    "COL_IT_VALOR_UNIT, " +
                    "COL_IT_VALOR, " +
                    "COL_IT_COD_PROD, " +
                    "COL_IT_BITOLA, " +
                    "COL_IT_MARCA, " +
                    "COL_IT_MODELO, " +
                    "COL_IT_SERIE, " +
                    "COL_IT_DOT, " +
                    "COL_IT_MONTADO, " +
                    "COL_IT_DESENHO, " +
                    "COL_IT_URGENTE, " +
                    "COL_IT_PERC_COM_BORR, " +
                    "COL_IT_VLR_COM_BORR, " +
                    "COL_IT_OBS, " +
                    "COL_IT_C_AGUA, " +
                    "COL_IT_C_CAMARA, " +
                    "COL_IT_GARANTIA, " +
                    "COL_IT_STATUS_ENVIO," +
                    "COL_IT_FILIAL_ORI," +
                    "COL_IT_COLETA_ORI," +
                    "COL_IT_ITEM_ORI" +
                    ") " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            stmt.setString(1, filial);
            stmt.setString(2, numero);
            stmt.setString(3, item);
            stmt.setString(4, data);
            stmt.setString(5, codCli);
            stmt.setString(6, lojaCli);
            stmt.setString(7, qtd);
            stmt.setDouble(8, Double.parseDouble(vlrUnit));
            stmt.setDouble(9, Double.parseDouble(total));
            stmt.setString(10, codPrd);
            stmt.setString(11, bitola);
            stmt.setString(12, marca);
            stmt.setString(13, modelo);
            stmt.setString(14, serie);
            stmt.setString(15, dot);
            stmt.setString(16, montado);
            stmt.setString(17, desenho);
            stmt.setString(18, urgente);
            stmt.setString(19, percComBorr);
            stmt.setDouble(20, vlrComBorr.contains(",") ? Double.parseDouble(vlrComBorr.replace(",", ".")) : Double.parseDouble(vlrComBorr));
            stmt.setString(21, observacao);
            stmt.setString(22, cAgua);
            stmt.setString(23, cCamara);
            stmt.setString(24, garantia);
            stmt.setString(25, "2"); //2 enviada
            stmt.setString(26, filialOri);
            stmt.setString(27, coletaOri);
            stmt.setString(28, itemOri);

            int rows = stmt.executeUpdate();
            stmt.close();
            connection.close();

            return rows > 0;

        } catch (SQLException expt) {
            deleteColeta(filial, data, numero, false);

            ConexaoBDInt db = new ConexaoBDInt(context);
            db.updColeta("UPDATE COLETA_ITENS " +
                    "SET COL_IT_STATUS_ENVIO = '1' " +
                    "WHERE COL_IT_FILIAL = '" + filial + "'" +
                    "  AND COL_IT_IDENTIF = '" + numero + "'" +
                    "  AND COL_IT_COD_CLI = '" + codCli + "'" +
                    "  AND COL_IT_LOJA_CLI = '" + lojaCli + "'" +
                    "  AND COL_IT_DATA = '" + data + "'");

            /* ATUALIZA O STATUS DA COLETA **/
            db.updColeta("UPDATE COLETA_CABEC " +
                    "SET COL_CABEC_STATUS_ENVIO = '1' " +
                    "WHERE COL_CABEC_FILIAL = '" + filial + "' " +
                    "  AND COL_CABEC_IDENTIF = '" + numero + "'" +
                    "  AND COL_CABEC_COD_CLI = '" + codCli + "'" +
                    "  AND COL_CABEC_LOJA_CLI = '" + lojaCli + "'" +
                    "  AND COL_CABEC_DATA = '" + data + "'");

            expt.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(expt.getMessage());
            Log.v("Coleta Insere Itens EXT", "", expt.fillInStackTrace());
            return false;
        }
    }

    public boolean insereDadosColetaCli(String filial, String numero, String data, String cod_cli, String loja_cli, String doc_cli, String ie, String rzSocial,
                                        String email, String ent_end, String ent_bairro,
                                        String ent_munic, String ent_est, String ent_cep, String ent_ddd, String ent_fone, String cobr_end,
                                        String cobr_bairro, String cobr_munic, String cobr_est, String cobr_cep, String cobr_ddd,
                                        String cobr_fone, String info_adic, String col_cli_rg, String col_cli_cpf, String col_cli_compResid, String col_cli_categoria) {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO COLETA_CLI " +
                    "(COL_CLI_FILIAL," +
                    "COL_CLI_IDENTIF," +
                    "COL_CLI_DATA," +
                    "COL_CLI_COD_CLI," +
                    "COL_CLI_LOJA_CLI," +
                    "COL_CLI_DOC_CLI," +
                    "COL_CLI_IE," +
                    "COL_CLI_RZ_SOCIAL," +
                    "COL_CLI_EMIAL," +
                    "COL_CLI_ENT_END," +
                    "COL_CLI_ENT_BAIRRO," +
                    "COL_CLI_ENT_MUNIC," +
                    "COL_CLI_ENT_EST," +
                    "COL_CLI_ENT_CEP," +
                    "COL_CLI_ENT_DDD," +
                    "COL_CLI_ENT_FONE," +
                    "COL_CLI_COBR_END," +
                    "COL_CLI_COBR_BAIRRO," +
                    "COL_CLI_COBR_MUNIC," +
                    "COL_CLI_COBR_EST," +
                    "COL_CLI_COBR_CEP," +
                    "COL_CLI_COBR_DDD," +
                    "COL_CLI_COBR_FONE," +
                    "COL_CLI_STATUS_ENVIO," +
                    "COL_CLI_INFO_ADIC," +
                    "COL_CLI_RG," +
                    "COL_CLI_CPF," +
                    "COL_CLI_COMP_RESID," +
                    "COL_CLI_CATEGORIA)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            stmt.setString(1, filial);
            stmt.setString(2, numero);
            stmt.setString(3, data);
            stmt.setString(4, cod_cli);
            stmt.setString(5, loja_cli);
            stmt.setString(6, doc_cli);
            stmt.setString(7, ie);
            stmt.setString(8, rzSocial);
            stmt.setString(9, email);
            stmt.setString(10, ent_end);
            stmt.setString(11, ent_bairro);
            stmt.setString(12, ent_munic);
            stmt.setString(13, ent_est);
            stmt.setString(14, ent_cep);
            stmt.setString(15, ent_ddd);
            stmt.setString(16, ent_fone);
            stmt.setString(17, cobr_end);
            stmt.setString(18, cobr_bairro);
            stmt.setString(19, cobr_munic);
            stmt.setString(20, cobr_est);
            stmt.setString(21, cobr_cep);
            stmt.setString(22, cobr_ddd);
            stmt.setString(23, cobr_fone);
            stmt.setString(24, "2");
            stmt.setString(25, info_adic);
            stmt.setString(26, col_cli_rg);
            stmt.setString(27, col_cli_cpf);
            stmt.setString(28, col_cli_compResid);
            stmt.setString(29, col_cli_categoria);

            int rows = stmt.executeUpdate();
            stmt.close();
            connection.close();

            return rows > 0;
        } catch (SQLException expt) {
            expt.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt(expt.getMessage());
            Log.v("Coleta Insere Cli EXT", "", expt.fillInStackTrace());

            return false;
        }
    }

    public void updDadosAuditApp(String codUsr, String campo, String dado) {
        try {
            ConnectToDatabase("A", GettersSetters.getConexaoBD());

            PreparedStatement stmt = connection.prepareStatement("UPDATE USUARIOS " +
                    "SET " + campo + " = '" + dado + "' " +
                    "WHERE ID = '" + codUsr + "'");

            int rows = stmt.executeUpdate();
            stmt.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updParametroAtualizacaoDados(String parametro) {
        try {
            ConnectToDatabase("C", GettersSetters.getConexaoBD());

            PreparedStatement stmt = connection.prepareStatement("UPDATE COLETA_PARAMETROS " +
                    "SET COL_CONTEUDO = (SELECT MAX(COL_CONTEUDO) + 1 " +
                    "                       FROM COLETA_PARAMETROS " +
                    "                     WHERE COL_PARAMETRO = '" + parametro + "') " +
                    "WHERE COL_PARAMETRO = '" + parametro + "'");

            int rows = stmt.executeUpdate();
            stmt.close();
            connection.close();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
