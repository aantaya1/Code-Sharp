package com.aantaya.codesharp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.AnswerActivity;
import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.ui.settings.SettingsActivity;
import com.aantaya.codesharp.utils.IntentUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.lang.ref.WeakReference;

import static com.aantaya.codesharp.ui.home.HomeViewModel.STATE_FAILED;
import static com.aantaya.codesharp.ui.home.HomeViewModel.STATE_LOADING;
import static com.aantaya.codesharp.ui.home.HomeViewModel.STATE_NORMAL;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel mHomeViewModel;
    private RecyclerView mRecyclerView;
    private QuestionsRecyclerViewAdapter mAdapter;
    private ShimmerFrameLayout mShimmerLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = root.findViewById(R.id.recyclerview_home);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Add a divider between items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new QuestionsRecyclerViewAdapter(new WeakReference<>(getContext()), new QuestionsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(RecyclerViewQuestionItem item, int position) {
                Intent intent = new Intent(getActivity(), AnswerActivity.class);
                intent.putExtra(IntentUtils.CLICKED_QUESTION_ID_EXTRA, item.getQuestionId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(RecyclerViewQuestionItem item, int position) {
                //todo: maybe if we want to do something with this
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mShimmerLayout = root.findViewById(R.id.shimmer_layout);

        //This tells android that we have a options menu that we would like to render
        // if we don't set this, the menu callbacks will not be called
        setHasOptionsMenu(true);

        Toolbar toolbar = root.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the view model
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //Initialize the view model with data
        mHomeViewModel.init();

        mHomeViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state){
                case STATE_NORMAL:
                    finishShowingLoadingView();
                    break;
                case STATE_LOADING:
                    startShowingLoadingView();
                    break;
                case STATE_FAILED:
                    //todo: what should we do here?
                    break;
            }
        });

        //Set up the fragment to observe changes to the questions as they are updated in the viewmodel
        //and then update the adapter with the new items
        mHomeViewModel.getQuestions().observe(getViewLifecycleOwner(),
                recyclerViewQuestionItems -> mAdapter.updateItems(recyclerViewQuestionItems));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.main_menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_more:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case R.id.main_menu_filter:
                //todo: need to implement
                Toast.makeText(getContext(), "Need to implement 7832", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startShowingLoadingView(){
        mShimmerLayout.startShimmer();
        mShimmerLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void finishShowingLoadingView(){
        mShimmerLayout.stopShimmer();
        mShimmerLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
