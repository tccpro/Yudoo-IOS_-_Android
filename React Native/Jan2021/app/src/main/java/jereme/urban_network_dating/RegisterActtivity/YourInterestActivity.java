package jereme.urban_network_dating.RegisterActtivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jereme.urban_network_dating.Adapters.CustomAdapter;
import jereme.urban_network_dating.List.InterestList;
import jereme.urban_network_dating.R;

public class YourInterestActivity extends AppCompatActivity {
    Button continues;
    ListView simpleList;
    String interestList[] = {"Movies", "Animals", "Sports & Fitness", "Arts", "Fashion", "Adventure"};
    int flags[] = {R.drawable.movies, R.drawable.puppy, R.drawable.batmitten, R.drawable.paint, R.drawable.fashion, R.drawable.temple};
    private List<InterestList> movielist = new ArrayList<>();
    ArrayList<ArrayList<InterestList>> interestlist = new ArrayList<ArrayList<InterestList>>();
    ArrayList<String> myinterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_interest);
        TextView btnnext = (TextView) findViewById(R.id.btnNext);
//        continues = (Button) findViewById(R.id.continues);

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myinterest = new ArrayList<>();

                for (int i = 0; i < interestlist.size(); i++) {
                    for (int j = 0; j < interestlist.get(i).size(); j++) {
                        if (interestlist.get(i).get(j).isSelected()) {
                            myinterest.add(interestlist.get(i).get(j).getText());
                            //  System.out.println(interestlist.get(i).get(j).getText());
                        }
                    }
                }

                String yourinterest = new Gson().toJson(myinterest);
                SharedPreferences saveUser = PreferenceManager.getDefaultSharedPreferences(YourInterestActivity.this);
                SharedPreferences.Editor editor = saveUser.edit();
                editor.putString("yourinterest", yourinterest);
                editor.commit();
                Intent intent = new Intent(YourInterestActivity.this, LookingActivity.class);
                startActivity(intent);
            }
        });

        InterestList movie = new InterestList("Action",false);
        movielist.add(movie);
        movie = new InterestList("Adventure",false);
        movielist.add(movie);
        movie = new InterestList("Animation",false);
        movielist.add(movie);
        movie = new InterestList("Comedy",false);
        movielist.add(movie);
        movie = new InterestList("Horror",false);
        movielist.add(movie);
        movie = new InterestList("Thriller",false);
        movielist.add(movie);
        movie = new InterestList("Romance",false);
        movielist.add(movie);
        movie = new InterestList("Documentary",false);
        movielist.add(movie);
        movie = new InterestList("Scary",false);
        movielist.add(movie);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList animal = new InterestList("Dog",false);
        movielist.add(animal);
        animal = new InterestList( "Cat",false);
        movielist.add(animal);
        animal = new InterestList("Snake",false);
        movielist.add(animal);
        animal = new InterestList( "Horse",false);
        movielist.add(animal);
        animal = new InterestList("Rabbit",false);
        movielist.add(animal);
        animal = new InterestList("Goat",false);
        movielist.add(animal);
        animal = new InterestList("Monkey",false);
        movielist.add(animal);
        animal = new InterestList( "Lion",false);
        movielist.add(animal);
        animal = new InterestList( "Tiger",false);
        movielist.add(animal);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList sports = new InterestList("Soccer",false);
        movielist.add(sports);
        sports = new InterestList( "Football",false);
        movielist.add(sports);
        sports = new InterestList("Basketball",false);
        movielist.add(sports);
        sports = new InterestList( "Hockey",false);
        movielist.add(sports);
        sports = new InterestList( "Bowling",false);
        movielist.add(sports);
        sports = new InterestList( "Pool",false);
        movielist.add(sports);
        sports = new InterestList( "Golf",false);
        movielist.add(sports);
        sports = new InterestList( "Tennis",false);
        movielist.add(sports);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList fitness = new InterestList("Running",false);
        movielist.add(fitness);
        fitness = new InterestList("Weightlifting",false);
        movielist.add(fitness);
        fitness = new InterestList("Swimming",false);
        movielist.add(fitness);
        fitness = new InterestList("Yoga",false);
        movielist.add(fitness);
        fitness = new InterestList("Martial Arts",false);
        movielist.add(fitness);
        fitness = new InterestList("Ballet",false);
        movielist.add(fitness);
        fitness = new InterestList("Aerobics",false);
        movielist.add(fitness);
        fitness = new InterestList("Zumba",false);
        movielist.add(fitness);
        fitness = new InterestList("Dancing",false);
        movielist.add(fitness);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList arts = new InterestList("Music",false);
        movielist.add(arts);
        arts = new InterestList( "Painting",false);
        movielist.add(arts);
        arts = new InterestList("Sculpture",false);
        movielist.add(arts);
        arts = new InterestList( "Sewing",false);
        movielist.add(arts);
        arts = new InterestList( "Spray Paint",false);
        movielist.add(arts);
        arts = new InterestList( "Singing",false);
        movielist.add(arts);
        arts = new InterestList( "Rapping",false);
        movielist.add(arts);
        arts = new InterestList( "Spoken Word",false);
        movielist.add(arts);
        arts = new InterestList( "Graphic Design",false);
        movielist.add(arts);
        arts = new InterestList( "Photoshop",false);
        movielist.add(arts);
        arts = new InterestList( "Acting",false);
        movielist.add(arts);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList beauty = new InterestList("Latest Trend",false);
        movielist.add(beauty);
        beauty = new InterestList( "Pop Culture",false);
        movielist.add(beauty);
        beauty = new InterestList("Hip Hop",false);
        movielist.add(beauty);
        beauty = new InterestList( "Business Attire",false);
        movielist.add(beauty);
        beauty = new InterestList( "Casual",false);
        movielist.add(beauty);
        beauty = new InterestList( "Fancy",false);
        movielist.add(beauty);
        beauty = new InterestList( "Hipster",false);
        movielist.add(beauty);
        beauty = new InterestList( "Original",false);
        movielist.add(beauty);
        beauty = new InterestList( "Sporty",false);
        movielist.add(beauty);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        InterestList outdoor = new InterestList("Road Trips",false);
        movielist.add(outdoor);
        outdoor = new InterestList( "Picnics",false);
        movielist.add(outdoor);
        outdoor = new InterestList("Camping",false);
        movielist.add(outdoor);
        outdoor = new InterestList( "Beaches",false);
        movielist.add(outdoor);
        outdoor = new InterestList( "Hiking",false);
        movielist.add(outdoor);
        outdoor = new InterestList( "Bar Hopper",false);
        movielist.add(outdoor);
        interestlist.add((ArrayList<InterestList>) movielist);
        movielist = new ArrayList<>();

        simpleList = (ListView) findViewById(R.id.simpleListView);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), interestList, flags, interestlist);
        simpleList.setAdapter(customAdapter);
    }
}
