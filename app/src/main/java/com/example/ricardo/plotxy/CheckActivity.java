package com.example.ricardo.plotxy;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class CheckActivity extends AppCompatActivity {
    private Switch swIndustria;
    private Switch swService;
    private Switch swHidraulica;
    private Button buttonConectar;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if(nfcAdapter==null){
            Toast.makeText(this,"Não existe NFC no aparelho!",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Existe NFC no aparelho!",Toast.LENGTH_SHORT).show();
        }

        swIndustria=(Switch) findViewById(R.id.swInd4);
        swService=(Switch) findViewById(R.id.swService);
        swHidraulica=(Switch) findViewById(R.id.swHidraulica);
        buttonConectar=(Button) findViewById(R.id.buttonConectar);
        buttonConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swIndustria.isChecked()){
                    Toast.makeText(CheckActivity.this,"Ativado1",Toast.LENGTH_SHORT).show();
                }
                if(swService.isChecked()){
                    Toast.makeText(CheckActivity.this,"Ativado2",Toast.LENGTH_SHORT).show();
                }
                if(swHidraulica.isChecked()){
                    Toast.makeText(CheckActivity.this,"Ativado3",Toast.LENGTH_SHORT).show();
                }

            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                Log.d("TESTE","NAO NULA");
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (getDec(id)==585767634){
                    Intent tela2=new Intent(CheckActivity.this,Main2Activity.class);
                    startActivity(tela2);
                }
            }
        }
    }
    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

}
