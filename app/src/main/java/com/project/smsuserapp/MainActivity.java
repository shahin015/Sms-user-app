package com.project.smsuserapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private  TextView maraque;
    RecyclerView recyclerView;
    private ArrayList<Data> list;
    private AdminAdpter adapter;

    private DatabaseReference reference;
    private StorageReference listref;
    private DatabaseReference maraques;

    String rfname="null";
    private LinearLayout layout;
    ProgressDialog pd;
    AdView mAdView;
    ////up date-- by nyeem


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maraque=findViewById(R.id.maraque);



        maraque.setSelected(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadFullscreenAd();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
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

      //  adView.setAdUnitId(getResources(R.string.banner_ad_unit_id));

        listref= FirebaseStorage.getInstance().getReference();

        ///hellow i m nayeem  update is go in on


        maraques=FirebaseDatabase.getInstance().getReference("maraq");
        maraques.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                maraque.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                maraque.setText(R.string.app_name);
            }
        });
        layout=findViewById(R.id.linear_layout);
        pd=new ProgressDialog(this);
        pd.setMessage("Loading ");
        pd.show();

        listref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                final List<String> reflist=new ArrayList<String>();
                for (StorageReference storageReference:listResult.getPrefixes()){

                    rfname= storageReference.getName();
                    reflist.add(0,rfname);
                    //  Toast.makeText(MainActivity.this, ""+rfname, Toast.LENGTH_SHORT).show();
                    reference= FirebaseDatabase.getInstance().getReference().child(rfname);
                    RecyclerView newrecyclerView=new RecyclerView(getApplicationContext());
                    TextView newText=new TextView(getApplicationContext());
                    newText.setText(rfname);
                    layout.addView(newrecyclerView);
                    newText.setPadding(1,1,1,1);
                    newText.setTextSize(20);

                    newText.setGravity(Gravity.CENTER);
                    newText.setAllCaps(false);
                    newText.setTextColor(getResources().getColor(R.color.blue));
                    layout.addView(newText);
                    newrecyclerView.setLayoutManager(new GridLayoutManager(getApplication(),2,GridLayoutManager.VERTICAL,false));
                    newrecyclerView.setHasFixedSize(true);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot){
                            list=new ArrayList<>();
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                Data data =snapshot1.getValue(Data.class);
                                list.add(0,data);
                                adapter=new AdminAdpter(MainActivity.this,list);
                                adapter.notifyDataSetChanged();
                                newrecyclerView.setAdapter(adapter);
                                pd.dismiss();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }
                    });

                }

            }
        });




    }





    InterstitialAd mInterstitialAd;

    // loadFullscreenAd method starts here.....
    private void loadFullscreenAd(){

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
                        loadFullscreenAd();
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


    private void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            ///  Toast.makeText(getApplicationContext(), "Ads Not redy", Toast.LENGTH_SHORT).show();
        }
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
        public AdminViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(context).inflate(R.layout.layout2,parent,false);
            return new AdminViewAdapter(view);


        }

        @Override
        public void onBindViewHolder(@NonNull AdminViewAdapter holder, @SuppressLint("RecyclerView") int position) {

            Data data=list.get(position);
            String foldername=data.getFoldername();



            Picasso.get().load(data.getImage()).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //showInterstitial();


                    Intent intent=new Intent(MainActivity.this, Viewar.class);
                    intent.putExtra("folder",foldername);
                    context.startActivity(intent);
                    finish();
                    showInterstitial();
                }
            });





        }

        @Override
        public int getItemCount() {
            int size=2;

            if (list==null||list.size()==1){
                size=0;
            }

            return size;
        }


        public class AdminViewAdapter extends RecyclerView.ViewHolder{
            private ImageView imageView;



            //   private Button link,titile;

            public AdminViewAdapter(@NonNull View itemView) {

                super(itemView);


                //  link=itemView.findViewById(R.id.youtube);
                ///  titile=itemView.findViewById(R.id.downloadbutton);
                imageView=itemView.findViewById(R.id.item_imageview1);

            }
        }

    }














    public static class Data extends Viewar.Data {
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