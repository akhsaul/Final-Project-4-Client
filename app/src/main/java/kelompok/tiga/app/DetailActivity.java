package kelompok.tiga.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import kelompok.tiga.app.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}