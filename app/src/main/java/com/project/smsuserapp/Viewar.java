package com.project.smsuserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Viewar extends AppCompatActivity {


    private AdminAdpter adapter;


    private ArrayList<Data> list;

    private DatabaseReference reference;
    private StorageReference listref;


    private LinearLayout layout2;
    String rfname="null";

    String refFoldername;

    String dw;
    ProgressDialog pd;

    AdView mAdView2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewar);





        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest);
        mAdView2.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        layout2=findViewById(R.id.linear_layout);
        pd=new ProgressDialog(this);
        pd.setMessage("Loading Photo");
        pd.show();
        refFoldername=getIntent().getStringExtra("folder");
     //   Toast.makeText(this, ""+refFoldername, Toast.LENGTH_SHORT).show();
        listref= FirebaseStorage.getInstance().getReference();

        reference= FirebaseDatabase.getInstance().getReference().child(refFoldername);
        RecyclerView newrecyclerView=new RecyclerView(getApplicationContext());

        layout2.addView(newrecyclerView);
        newrecyclerView.setLayoutManager(new LinearLayoutManager(getApplication(),LinearLayoutManager.VERTICAL,false));
        newrecyclerView.setHasFixedSize(true);




        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){

                list=new ArrayList<>();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Data data =snapshot1.getValue(Data.class);
                    list.add(0,data);

                }
                adapter=new AdminAdpter(Viewar.this,list);
                // adapter=new UserData(Viewar.this,list);
              ;
                newrecyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                pd.dismiss();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });
        loadFullscreenAd2();












    }

    InterstitialAd mInterstitialAd;

    // loadFullscreenAd method starts here.....
    private void loadFullscreenAd2(){

        //Requesting for a fullscreen Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;



                //Fullscreen callback || Requesting again when an ad is shown already
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.

                        //User dismissed the previous ad. So we are requesting a new ad here
                        loadFullscreenAd2();
                    }

                }); // FullScreen Callback Ends here


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error

                mInterstitialAd = null;

            }

        });

    }
    // loadFullscreenAd method ENDS  here..... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private void showInterstitial2() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            ///  Toast.makeText(getApplicationContext(), "Ads Not redy", Toast.LENGTH_SHORT).show();
        }
    }











    @Override
    public void onBackPressed() {

        Intent intent =new Intent(Viewar.this,MainActivity.class);
        startActivity(intent);
    }


    public class AdminAdpter extends RecyclerView.Adapter<AdminAdpter.AdminViewAdapter>{
        private ArrayList<Data> list;
        private Context context;

        private String categorys;


        public AdminAdpter(Context context, ArrayList<Data> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public AdminAdpter.AdminViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
            return new AdminAdpter.AdminViewAdapter(view);


        }

        @Override
        public void onBindViewHolder(@NonNull AdminAdpter.AdminViewAdapter holder, @SuppressLint("RecyclerView") int position) {

            String sms;
            Data data=list.get(position);
            String key= data.getKey();
            if (data.getYoutubetitile()!=null){
                sms=data.getYoutubetitile().toString();
            }else {
                sms="No sms Found";
            }
            if (data.getFoldername()!=null){
                holder.Sms_name.setText(data.getFoldername().toString());

            }else {
                holder.Sms_name.setText(R.string.app_name);

            }
            dw=data.getImage();
            holder.download.setText(data.getYoutubetitile());

            Picasso.get().load(data.getImage()).into(holder.imageView);
            String finalSms = sms;
            holder.copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("label", finalSms);
                    clipboardManager.setPrimaryClip(clipData);
                    showInterstitial2();
                    Toast.makeText(context, "Copy sms", Toast.LENGTH_SHORT).show();


                }
            });





        }

        @Override
        public int getItemCount() {


            return list.size();
        }


        public class AdminViewAdapter extends RecyclerView.ViewHolder{
            private ImageView imageView;
            private TextView download,Sms_name;

            private Button copy,shareinfb;

            public AdminViewAdapter(@NonNull View itemView) {

                super(itemView);

                Sms_name=itemView.findViewById(R.id.smsName);
                copy=itemView.findViewById(R.id.copy);
                download=itemView.findViewById(R.id.dwonliadbotton);
                imageView=itemView.findViewById(R.id.item_imageview);             }
        }


        private void goturl(String s){
            Uri uri=Uri.parse(s);
            context.startActivity(new Intent(Intent.ACTION_VIEW,uri));
        }







    }






    public static class Data {
        String youtubelink,youtubetitile,image,key,foldername;

        public String getFoldername() {
            return foldername;
        }

        public void setFoldername(String foldername) {
            this.foldername = foldername;
        }

        public Data(String foldername) {
            this.foldername = foldername;
        }

        public Data(String youtubelink, String youtubetitile, String image, String key, String foldername){

            this.youtubelink=youtubelink;
            this.youtubetitile=youtubetitile;
            this.image=image;
            this.key=key;
            this.foldername=foldername;
        }

        public Data() {
        }

        public String getYoutubelink() {
            return youtubelink;
        }

        public void setYoutubelink(String youtubelink) {
            this.youtubelink = youtubelink;
        }

        public String getYoutubetitile() {
            return youtubetitile;
        }

        public void setYoutubetitile(String youtubetitile) {
            this.youtubetitile = youtubetitile;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }


}