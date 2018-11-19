package dv106.refaat.themediaplayer2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        TextView www=(TextView)findViewById(R.id.textView);
        ImageView eee=(ImageView)findViewById(R.id.image111);
        TextView www1=(TextView)findViewById(R.id.textView2);




        TextView www2=(TextView)findViewById(R.id.textView3);
        www2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent www2 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/search?q=refaat%20al%20ktifan&c=apps"));
                startActivity(www2);

            }
        });
    }
}
