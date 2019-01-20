package ekaraatayforiraq.ekaraaty.com.ekaraaty;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class Favorite extends AppCompatActivity {
    DBCon dbCon;
    List<AllDataModel> HomeData;
    FirebaseFirestore firebaseFirestore;
    RecyclerView HomeDataList;
    LottieAnimationView loading;
    ConstraintLayout emptyView;
    private HomeDataAdapter homeDataAdapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        //layout toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //find views by there ids
        HomeDataList = (RecyclerView) findViewById(R.id.recyclerView2);
        emptyView = findViewById(R.id.emptyView);
        loading = findViewById(R.id.loading);

        //before the data is loading make the list Invisible and display loading animation
        HomeDataList.setVisibility(View.GONE);

        //start loading animation
        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();

        //implement back button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
                                          }


        //initial Firebase connection
        firebaseFirestore = FirebaseFirestore.getInstance();
        //get the current log In user
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        //check if there is no user currently log in display empty view
        if (user == null) {
            HomeDataList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            loading.cancelAnimation();
            //else if there is user start fetching the data
                           }
           else {
            //list  to display data
            HomeData = new ArrayList<>();
            //create Adapter for data
            homeDataAdapter = new HomeDataAdapter(HomeData, "fa", this);
            //create reference to database
            //initial RecyclerView
            HomeDataList.setLayoutManager(new LinearLayoutManager(this));
            HomeDataList.setAdapter(homeDataAdapter);

            //get the reference of the Favorite post that have been saved in sqlite a
            getFavData("houses/baghdad/karkh");
            getFavData("houses/baghdad/rusafa");
            getFavData("houses/otherCities/center");
            getFavData("houses/otherCities/township");
        }
    }

    //get the fav  data from firebase
    private void getFavData(final String ref) {
        //access to sqlite table
        dbCon = new DBCon(this);
        dbCon.getWritableDatabase();
        ArrayList<String> list = dbCon.getInfo(ref);
        //after getting the post reference start fetching post details from firbase collection
        if (list.size() > 0)
            for (int i = 0; i < list.size(); i++) {
                firebaseFirestore.collection(ref).document(list.get(i)).get().
                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //if there is no document saved as fav display empty view for user
                                if (!documentSnapshot.exists()) {
                                    emptyView.setVisibility(View.VISIBLE);
                                    HomeDataList.setVisibility(View.GONE);
                                    loading.setVisibility(View.GONE);
                                    loading.cancelAnimation();
                                    //else display the post in recycler view
                                } else {
                                    emptyView.setVisibility(View.GONE);
                                    HomeDataList.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);
                                    loading.cancelAnimation();
                                    //fill the model
                                    AllDataModel homeDataModel = documentSnapshot.toObject(AllDataModel.class).withId(documentSnapshot.getId());
                                    homeDataModel.setRefrence(ref);
                                    HomeData.add(homeDataModel);
                                    // notify the adapter to display data
                                    homeDataAdapter.notifyDataSetChanged();
                                }

                            }
                            // if the connection failure display empty view
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        HomeDataList.setVisibility(View.GONE);
                        loading.cancelAnimation();
                        loading.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
            }

    }

    //back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) ;
        finish();
        return super.onOptionsItemSelected(item);

    }
}
