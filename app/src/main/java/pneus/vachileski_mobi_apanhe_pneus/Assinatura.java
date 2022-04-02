package pneus.vachileski_mobi_apanhe_pneus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings("SpellCheckingInspection")
public class Assinatura extends AppCompatActivity {
    Button btnLimparAssin, btnConfAssin, btnCancelarAssin;
    File file, fileNoMedia;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;

    public static File DIRECTORY;///Environment.getExternalStorageDirectory().getAbsolutePath()+"/VachileskiMobi/Assinaturas/"; //Environment.getDataDirectory().getAbsolutePath() + "/VachileskiMobi/Assinaturas/";
    public static String pic_name = "";
    public String StoredPath = "";

    Vibrator vibe = null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint({"SourceLockedOrientationActivity", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_assinatura);

        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        if (GettersSetters.getTipoAssinatura().equals("CLI")) {
            pic_name = "Cliente-NE-" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
        } else {
            pic_name = "Borr-NE-" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
        }

        mContent = findViewById(R.id.llAssinatura);
        btnLimparAssin = findViewById(R.id.btnLimparAssin);
        btnConfAssin = findViewById(R.id.btnConfAssin);
        btnCancelarAssin = findViewById(R.id.btnCancelarAssin);

        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mSignature.setBackground(getDrawable(R.drawable.borda_assinatura));

        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        btnConfAssin.setVisibility(View.GONE);
        btnLimparAssin.setVisibility(View.GONE);

        view = mContent;
        btnConfAssin.setOnClickListener(onButtonClick);
        btnLimparAssin.setOnClickListener(onButtonClick);
        btnCancelarAssin.setOnClickListener(onButtonClick);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            btnConfAssin.setVisibility(View.GONE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        //CRIA AS PASTAS NECESSÁRIAS
        DIRECTORY = Assinatura.this.getExternalFilesDir("Assinaturas"); //Environment.getExternalStorageDirectory().getAbsolutePath()+"/VachileskiMobi/Assinaturas/"; //Environment.getDataDirectory().getAbsolutePath() + "/VachileskiMobi/Assinaturas/";
        StoredPath = DIRECTORY + "/" + pic_name;

//        File fileRoot = new File(String.valueOf(Assinatura.this.getExternalFilesDir("Assinaturas")));
//        if (!fileRoot.exists()) {
//            fileRoot.mkdirs();
//        }

        //DELETA OS ARQUIVOS DA PASTA ASSINATURAS
        if (!GettersSetters.isIsSessionAssinat()) {
            String[] files = DIRECTORY.list();
            File file;
            if (files != null && files.length > 0) {
                for (String nome : files) {
                    file = new File(DIRECTORY + "/" + nome);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            GettersSetters.setIsSessionAssinat(true);
        }

        file = new File(String.valueOf(DIRECTORY));

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }

        fileNoMedia = new File(file + "/" + ".nomedia");
        try {
            FileOutputStream mFileOutStream = new FileOutputStream(DIRECTORY + "/" + ".nomedia");
            mFileOutStream.flush();
            mFileOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnLimparAssin) {
                mSignature.clear();
                btnConfAssin.setVisibility(View.GONE);
                btnLimparAssin.setVisibility(View.GONE);
            } else if (v == btnConfAssin) {
                try {
                    btnConfAssin.setVisibility(View.GONE);
                    btnLimparAssin.setVisibility(View.GONE);
                    btnCancelarAssin.setVisibility(View.GONE);

                    mSignature.save(view, StoredPath);
                    Canvas canvas = new Canvas(bitmap);
                    Drawable background = view.getBackground();

                    if (background != null) {
                        background.draw(canvas);
                    }
                    view.draw(canvas);
                    recreate();

                } catch (Exception err) {
                    btnCancelarAssin.setVisibility(View.GONE);
                    Toast.makeText(Assinatura.this, "Erro ao criar bitmap: " + err, Toast.LENGTH_LONG).show();
                }
                //}
            } else if (v == btnCancelarAssin) {
                mSignature.clear();
                file = null;
                bitmap = null;
                Assinatura.this.finish();
            }
        }
    };

//    public boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

//                mSignature.save(view, StoredPath);
//                Canvas canvas = new Canvas(bitmap);
//                Drawable background = view.getBackground();
//
//                if (background != null) {
//                    background.draw(canvas);
//                }
//                bitmap.isMutable();
//                view.draw(canvas);

                //view.setDrawingCacheEnabled(true);

                Toast.makeText(this, "Permissão de armazenamento concedida!", Toast.LENGTH_SHORT).show();
                //btnConfAssin.setVisibility(View.VISIBLE);
                //btnLimparAssin.setVisibility(View.VISIBLE);
                // Calling the same class
                //recreate();
            } else {
                Toast.makeText(this, "O aplicativo não tem permissão para gravar em seu armazenamento. " +
                        "Portanto, ele não pode funcionar corretamente. Considere conceder essa permissão!", Toast.LENGTH_SHORT).show();
                //System.out.println("Entou aqui");
                //recreate();
            }
        }
    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            view.setDrawingCacheEnabled(true);
//            //mSignature.save(view, StoredPath);
//            Toast.makeText(getApplicationContext(), "Permissão de armazenamento concedida!", Toast.LENGTH_SHORT).show();
//            btnConfAssin.setVisibility(View.VISIBLE);
//            btnLimparAssin.setVisibility(View.VISIBLE);
//            // Calling the same class
//            //recreate();
//        } else {
//            Toast.makeText(this, "O aplicativo não tem permissão para gravar em seu armazenamento. " +
//                                              "Portanto, ele não pode funcionar corretamente. Considere conceder essa permissão!", Toast.LENGTH_LONG).show();
//            //recreate();
//        }
//    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 1.5f; //Espessura do risco
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final Paint paint = new Paint();
        private final Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @SuppressLint("WrongThread")
        public void save(View v, String StoredPath) {
            //Log.v("log_tag", "Width: " + v.getWidth());
            //Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                if (GettersSetters.getTipoAssinatura().equals("CLI")) {
                    GettersSetters.setPathAssinCli(DIRECTORY + "/");
                    GettersSetters.setPicNameAssinCli(pic_name);
                    Assinatura.this.finish();
                    ColetaConclusao.setAssinaturaCli();
                } else {
                    GettersSetters.setPathAssinBorr(DIRECTORY + "/");
                    GettersSetters.setPicNameAssinBorr(pic_name);
                    Assinatura.this.finish();
                    ColetaBorracheiro.setAssinaturaBorr();
                }
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag_save_sign", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
            btnConfAssin.setVisibility(GONE);
            btnLimparAssin.setVisibility(GONE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            btnConfAssin.setVisibility(View.VISIBLE);
            btnLimparAssin.setVisibility(View.VISIBLE);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
