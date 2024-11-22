package com.khi.scvotingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.khi.scvotingapp.data.Candidate;
import com.khi.scvotingapp.data.CandidateResult;
import com.khi.scvotingapp.data.CandidateVoted;
import com.khi.scvotingapp.data.PartylistResult;
import com.khi.scvotingapp.data.Position;
import com.khi.scvotingapp.manager.CandidateManager;
import com.khi.scvotingapp.manager.PartylistManager;
import com.khi.scvotingapp.manager.PositionManager;
import com.khi.scvotingapp.manager.VoteManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {

    private LinearLayout nothingLayout;
    private LinearLayout loadingLayout;

    private RecyclerView rvPositions;
    private RecyclerView rvPartylists;

    private Button btnRefresh;
    private Button btnViewVoted;
    private Button btnCandidates;
    private Button btnPartylists;


    private CandidateManager candidateManager;
    private PositionManager positionManager;
    private PartylistManager partylistManager;

    private PositionsAdapter positionAdapter;
    private PartylistsAdapter partylistsAdapter;

    private ArrayList<CandidateResult> candidates;
    private ArrayList<Position> positions;
    private ArrayList<PartylistResult> partylists;
    private ArrayList<CandidateVoted> votedCandidates;
    private ArrayList<Position> votedPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize(savedInstanceState);
        initializeLogic();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialize(Bundle savedInstanceState) {
        candidateManager = new CandidateManager();
        positionManager = new PositionManager();
        partylistManager = new PartylistManager();

        nothingLayout = findViewById(R.id.nothingLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        rvPositions = findViewById(R.id.rvPositions);
        rvPartylists = findViewById(R.id.rvPartylists);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnViewVoted = findViewById(R.id.btnViewVoted);
        btnCandidates = findViewById(R.id.btnCandidates);
        btnPartylists = findViewById(R.id.btnPartylists);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAll();
            }
        });

        btnViewVoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnViewVoted.setEnabled(false);

                showVoted();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnViewVoted.setEnabled(true);
                    }
                }, 1000);
            }
        });

        btnCandidates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(0);
            }
        });

        btnPartylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(1);
            }
        });
    }

    private void selected(int index) {
        rvPartylists.setVisibility(View.GONE);
        rvPositions.setVisibility(View.GONE);

        if (index == 0)
            rvPositions.setVisibility(View.VISIBLE);

        if (index == 1)
            rvPartylists.setVisibility(View.VISIBLE);

    }

    private void initializeLogic() {
        selected(0);
        loadAll();
    }

    private void loadAll() {
        loadPositions();
        loadPartylists();
    }

    private void loadPositions() {
        setLoading(true);

        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    positions = positionManager.getAllPositions();
                    candidates = candidateManager.getCandidatesResult();

                    if (positions.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvPositions.setLayoutManager(new LinearLayoutManager(ResultActivity.this));

                                positionAdapter = new PositionsAdapter(ResultActivity.this ,positions, candidates);
                                rvPositions.setAdapter(positionAdapter);

                                setLoading(false);
                                nothingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnViewVoted.setEnabled(true);
                                nothingLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } catch (SQLException e) {
                    Log.e("loadPositions()", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ResultActivity.this, "Failed to load candidates.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                    }
                });
            }
        });
    }

    private void loadPartylists() {
        setLoading(true);

        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    partylists = partylistManager.getPartylistResult();

                    if (partylists.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvPartylists.setLayoutManager(new LinearLayoutManager(ResultActivity.this));

                                partylistsAdapter = new PartylistsAdapter(ResultActivity.this, partylists);
                                rvPartylists.setAdapter(partylistsAdapter);

                                setLoading(false);
                                nothingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnViewVoted.setEnabled(true);
                                nothingLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } catch (SQLException e) {
                    Log.e("loadPartylists()", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ResultActivity.this, "Failed to load partylists.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                    }
                });
            }
        });
    }

    private void showVoted() {
        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    votedPositions = positionManager.getAllPositions();
                    votedCandidates = candidateManager.getCandidatesVoted();
                } catch (SQLException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ResultActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("showVoted()", e.getMessage());
                }

                String temp = "";
                int temp2;
                for (int i = 0; i < votedPositions.size(); i++) {
                    temp = temp + votedPositions.get(i).getName() + ":\n";
                    temp2 = 0;

                    for (int j = 0; j < votedCandidates.size(); j++) {
                        if (votedCandidates.get(j).getPositionID() == votedPositions.get(i).getId()) {
                            temp = temp + votedCandidates.get(j).getName() + "\n";
                            temp2++;
                        }
                    }
                    if (temp2 == 0)
                        temp = temp + "N/A\n";

                    if (i != votedPositions.size() - 1)
                        temp = temp + "\n";
                }

//                for (int i = 0; i < votedPositions.size(); i++) {
//                    temp = temp + votedPositions.get(i).getName() + "\n";
//                }
//
//                for (int j = 0; j < votedCandidates.size(); j++) {
//                    temp = temp + votedCandidates.get(j).getPosition() + ":\n" + votedCandidates.get(j).getName();
//                    if (j != votedCandidates.size() - 1) {
//                        temp = temp + "\n\n";
//                    }
//                }

                String votedStr = temp;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (votedCandidates.size() > 0) {
                            new MaterialAlertDialogBuilder(ResultActivity.this)
                                    .setTitle("Voted Candidates")
                                    .setMessage(votedStr)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ResultActivity.this, "You haven’t submitted any votes yet.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });
            }
        });
    }

    private void setLoading(boolean load) {
        if (load) {
            loadingLayout.setVisibility(View.VISIBLE);
            rvPositions.setEnabled(false);
            btnViewVoted.setEnabled(false);
            btnRefresh.setEnabled(false);
            return;
        }
        loadingLayout.setVisibility(View.GONE);
        rvPositions.setEnabled(true);
        btnViewVoted.setEnabled(true);
        btnRefresh.setEnabled(true);
    }


    private class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Position> data;
        private ArrayList<CandidateResult> canData;

        public PositionsAdapter(Context context, ArrayList<Position> data, ArrayList<CandidateResult> canData) {
            this.context = context;
            this.data = data;
            this.canData = canData;
        }

        @NonNull
        @Override
        public PositionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.layout_position, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new PositionsAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PositionsAdapter.ViewHolder holder, int position) {
            initialize(holder, position);

        }

        // code here
        private void initialize(PositionsAdapter.ViewHolder holder, int position) {
            View view = holder.itemView;
            Position positionData = data.get(position);

            final TextView txtPosition = view.findViewById(R.id.txtPosition);
            final TextView txtDescription = view.findViewById(R.id.txtDescription);
            final RecyclerView rvCandidates = view.findViewById(R.id.rvCandidates);
            final LinearLayout sepTop = view.findViewById(R.id.seperateTop);

            if (position == 0)
                sepTop.setVisibility(View.VISIBLE);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

            txtPosition.setText(positionData.getName());

            txtDescription.setVisibility(View.GONE);


            ExecutorService executor;
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false);
                            layoutManager.setInitialPrefetchItemCount(getItemCount());

                            rvCandidates.setLayoutManager(layoutManager);
                        }
                    });

                    int maxProgress = 0;
                    ArrayList<CandidateResult> newCand = new ArrayList<>();
                    for (int i = 0; i < canData.size(); i++) {
                        if (canData.get(i).getPositionID() == positionData.getId()) {
                            if (maxProgress < canData.get(i).getTotalVotes())
                                maxProgress = canData.get(i).getTotalVotes();

                            newCand.add(canData.get(i));
                        }
                    }

                    int max = maxProgress;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvCandidates.setAdapter(new CandidatesAdapter(context, newCand, max));
                        }
                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        private class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.ViewHolder> {

            private ArrayList<CandidateResult> data;
            private int maxProgress;
            private Context context;

            public CandidatesAdapter(Context context, ArrayList<CandidateResult> data, int maxProgress) {
                this.data = data;
                this.maxProgress = maxProgress;
                this.context = context;
            }

            @NonNull
            @Override
            public CandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.layout_candidate_result, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(lp);
                return new CandidatesAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull CandidatesAdapter.ViewHolder holder, int position) {
                initialize(holder, position);

            }

            // code here
            private void initialize(CandidatesAdapter.ViewHolder holder, int position) {
                View view = holder.itemView;
                CandidateResult candidate = data.get(position);

                final LinearLayout main_layout = view.findViewById(R.id.main_layout);
                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtProgram = view.findViewById(R.id.txtProgram);
                final TextView txtTotalVotes = view.findViewById(R.id.txtTotalVotes);
                final ProgressBar progTotalVotes = view.findViewById(R.id.progTotalVotes);

                progTotalVotes.setMax(maxProgress);
                txtTotalVotes.setText(String.valueOf(candidate.getTotalVotes()));
                progTotalVotes.setProgress(candidate.getTotalVotes());

                txtName.setText(candidate.getName());
                txtProgram.setText(candidate.getPartylist() + " - " + candidate.getProgram());

                main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // idk
                    }
                });
            }

            @Override
            public int getItemCount() {
                return data.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                }
            }
        }

    }

    private class PartylistsAdapter extends RecyclerView.Adapter<PartylistsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<PartylistResult> data;

        public PartylistsAdapter(Context context, ArrayList<PartylistResult> data) {
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public PartylistsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.layout_partylist_result, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new PartylistsAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PartylistsAdapter.ViewHolder holder, int position) {
            initialize(holder, position);

        }

        // code here
        private void initialize(PartylistsAdapter.ViewHolder holder, int position) {
            View view = holder.itemView;
            PartylistResult partylistResultData = data.get(position);

            final TextView txtName = view.findViewById(R.id.txtName);
            final TextView txtTotalVotes = view.findViewById(R.id.txtTotalVotes);
            final ProgressBar progTotalVotes = view.findViewById(R.id.progTotalVotes);
            final LinearLayout sepTop = view.findViewById(R.id.seperateTop);

            if (position == 0)
                sepTop.setVisibility(View.VISIBLE);

            progTotalVotes.setMax(data.get(0).getTotalVotes());
            txtTotalVotes.setText(String.valueOf(partylistResultData.getTotalVotes()));
            progTotalVotes.setProgress(partylistResultData.getTotalVotes());

            txtName.setText(partylistResultData.getPartylistName());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}