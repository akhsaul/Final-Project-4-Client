package kelompok.tiga.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import kelompok.tiga.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}