package com.blinkreceipt.ocr.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.databinding.ActivityCameraScanBinding;
import com.microblink.BitmapResult;
import com.microblink.CameraCaptureListener;
import com.microblink.CameraRecognizerCallback;
import com.microblink.Media;
import com.microblink.RecognizerException;
import com.microblink.RecognizerResult;
import com.microblink.RecognizerView;
import com.microblink.core.ScanResults;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kotlin.Unit;

public class CameraActivity extends AppCompatActivity implements CameraRecognizerCallback, CameraCaptureListener {

    private ActivityCameraScanBinding binding;

    private RecognizerView recognizerView;

    private Button finishScan;

    private View torch;

    private boolean isTorchOn = false;

    // Fallback only (SDK < 2.2.2): client-managed session flag used in place of
    // RecognizerView.initialized(). Set true after a successful initialize(), cleared in onDestroy().
    // private boolean sessionActive = false;

    private final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(this.getWindow());

        binding = ActivityCameraScanBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() |
                            WindowInsetsCompat.Type.displayCutout()
            );
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });
        ViewGroupCompat.installCompatInsetsDispatch(rootView);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.getWindow().setStatusBarContrastEnforced(true);
        }

        recognizerView = findViewById(R.id.recognizer);

        finishScan = findViewById(R.id.finish_scan);

        finishScan.setOnClickListener(view -> {
            try {
                Toast.makeText(getApplicationContext(), R.string.finishing, Toast.LENGTH_SHORT).show();

                view.setEnabled(false);

                recognizerView.finishedScanning();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        torch = findViewById(R.id.torch);

        torch.setOnClickListener(v -> recognizerView.setTorchState(!isTorchOn, success -> {
            if (success) {
                isTorchOn = !isTorchOn;
            }

            return Unit.INSTANCE;
        }));

        final Button captureFrame = findViewById(R.id.capture_photo);

        captureFrame.setOnClickListener(v -> recognizerView.takePicture());

        recognizerView.recognizerCallback(this);
        recognizerView.cameraCaptureListener(this);

        try {
            recognizerView.initialize(Objects.requireNonNull(getIntent()
                    .getParcelableExtra(MainActivity.SCAN_OPTIONS_EXTRA)));

            // sessionActive = true;   // fallback only (SDK < 2.2.2)
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

            finish();
        }

        recognizerView.lifecycle(this);
    }

    @Override
    public void onRecognizerDone(@NonNull ScanResults results, @NonNull Media media) {
        finishScan.setEnabled(true);

        setResult(Activity.RESULT_OK, new Intent()
                .putExtra(MainActivity.DATA_EXTRA, results)
                .putExtra(MainActivity.MEDIA_EXTRA, media));

        finish();
    }

    @Override
    public void onRecognizerException(@NonNull Throwable throwable) {
        finishScan.setEnabled(true);

        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {
        Log.d(TAG, "results: " + result);
    }

    @Override
    public void onConfirmPicture(@NonNull File file) {
        Log.d(TAG, file.toString());
    }

    @Override
    public void onPermissionDenied() {
    }

    @Override
    public void onPreviewStarted() {
        if (recognizerView.isCameraTorchSupported()) {
            torch.setVisibility(View.VISIBLE);
        } else {
            torch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCaptured(@NonNull BitmapResult bitmapResult) {
        if (!isRecognizerSessionActive()) {
            Log.d(TAG, "onCaptured ignored, recognizer session is no longer active");

            return;
        }

        try {
            recognizerView.confirmPicture(bitmapResult);
        } catch (RecognizerException | IllegalStateException e) {
            Log.e(TAG, "failure in confirmPicture", e);

            return;
        }

        Toast.makeText(getApplicationContext(), R.string.captured_photo, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(@NonNull Throwable throwable) {
        Log.e(TAG, "failure in onException", throwable);

        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * Guards re-entering {@link #recognizerView} from the async capture callback: the callback
     * can still fire after the screen has started tearing down or after the shared recognizer
     * session has been terminated (e.g. by a stale reference on screen re-entry), in which case
     * touching the view would throw.
     */
    // --- Primary: SDK >= 2.2.2 (public RecognizerView.initialized() available) ---
    private boolean isRecognizerSessionActive() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)
                && recognizerView.initialized();
    }

    // --- Fallback: SDK < 2.2.2 (no public initialized() API) ---
    // Uses a client-managed session flag instead of the SDK state query. The try/catch in
    // onCaptured remains the real backstop; this flag just avoids most doomed re-entries.
    // Requires the `sessionActive` field, `sessionActive = true;` after a successful initialize(),
    // and `sessionActive = false;` in onDestroy().
    //
    // private boolean isRecognizerSessionActive() {
    //     return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)
    //             && sessionActive;
    // }

    // Fallback only (SDK < 2.2.2): clears the client-managed session flag on teardown.
    //
    // @Override
    // protected void onDestroy() {
    //     sessionActive = false;
    //
    //     super.onDestroy();
    // }

}
