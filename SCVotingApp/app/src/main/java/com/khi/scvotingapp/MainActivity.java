package com.khi.scvotingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.khi.scvotingapp.data.Candidate;
import com.khi.scvotingapp.data.CandidateVoted;
import com.khi.scvotingapp.manager.CandidateManager;
import com.khi.scvotingapp.data.Election;
import com.khi.scvotingapp.manager.ElectionManager;
import com.khi.scvotingapp.data.Position;
import com.khi.scvotingapp.manager.PositionManager;
import com.khi.scvotingapp.manager.SignInManager;
import com.khi.scvotingapp.manager.VoteManager;
import com.khi.scvotingapp.util.AppUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView txtElectionTitle;
    private TextView txtDescription;
    private TextView txtErrorDescription;

    private Button btnPreview;
    private Button btnSubmit;
    private Button btnViewResult;

    private RecyclerView rvPositions;

    private LinearLayout loadingLayout;
    private LinearLayout nothingLayout;

    private PositionsAdapter positionAdapter;

    private CandidateManager candidateManager;
    private PositionManager positionManager;
    private VoteManager voteManager;

    private ArrayList<Candidate> candidates;
    private ArrayList<Position> positions;
    private ArrayList<CandidateVoted> votedCandidates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) System.exit(0);
        initialize(savedInstanceState);
        initializeLogic();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isLoaded", true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void initialize(Bundle savedInstanceState) {
        candidateManager = new CandidateManager();
        positionManager = new PositionManager();
        voteManager = new VoteManager();

        txtElectionTitle = findViewById(R.id.txtElectionTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtErrorDescription = findViewById(R.id.txtErrorDescription);
        btnPreview = findViewById(R.id.btnPreview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewResult = findViewById(R.id.btnViewResult);
        rvPositions = findViewById(R.id.rvPositions);
        loadingLayout = findViewById(R.id.loadingLayout);
        nothingLayout = findViewById(R.id.nothingLayout);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateVotes();
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPreview.setEnabled(false);

                showPreview();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnPreview.setEnabled(true);
                    }
                }, 1000);
            }
        });

        btnViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnViewResult.setEnabled(false);
                startActivity(new Intent(MainActivity.this, ResultActivity.class));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnViewResult.setEnabled(true);
                    }
                }, 1000);
            }
        });
    }

    private void initializeLogic() {
        setLoading(true);
        setError("");

        loadElection();
    }

    private void loadElection() {
        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ElectionManager electionManager = new ElectionManager();
                try {
                    Election election = electionManager.getElectionDetails();

                    if (election != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtElectionTitle.setText(election.getTitle());
                                txtDescription.setText("Hello, " + AppUtil.toTitleCase(SignInManager.getInstance().getName()) + ". " + election.getDescription());
                            }
                        });

                        String status = electionManager.checkElectionStatus(election);
                        if (!status.equals("The election is ongoing.")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialAlertDialogBuilder(MainActivity.this)
                                            .setTitle("Election Status")
                                            .setMessage(status)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                finish();
                                                System.exit(0);
                                            })
                                            .show();
                                }
                            });
                            return;
                        }
                        loadPositions();
                    }
                } catch (SQLException e) {
                    Log.e("loadElection()", e.getMessage());
                }
            }
        });
    }

    private void loadPositions() {
        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    positions = positionManager.getAvailablePositions();
                    candidates = candidateManager.getCandidates();

                    if (positions.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvPositions.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                positionAdapter = new PositionsAdapter(MainActivity.this ,positions, candidates);
                                rvPositions.setAdapter(positionAdapter);

                                setLoading(false);
                                nothingLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnViewResult.setEnabled(true);
                                btnViewResult.setText("View Result");
                                btnSubmit.setVisibility(View.GONE);
                                btnPreview.setVisibility(View.GONE);
                                nothingLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } catch (SQLException e) {
                    Log.e("loadPositions()", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to load candidates", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showPreview() {
        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String votes = "";
                int temp = 0;
                int temp2 = 0;

                // do in background
                ArrayList<Candidate> selectedCandidate = positionAdapter.canData;
                ArrayList<Position> selectedPosition = positionAdapter.data;

                for (int i = 0; i < selectedPosition.size(); i++) {
                    votes = votes + selectedPosition.get(i).getName() + ":\n";
                    temp2 = 0;

                    for (int j = 0; j < selectedCandidate.size(); j++) {
                        if (selectedCandidate.get(j).getPositionID() == selectedPosition.get(i).getId()) {
                            if (selectedCandidate.get(j).isSelected()) {
                                votes = votes + selectedCandidate.get(j).getName() + "\n";
                                temp++;
                                temp2++;
                            }
                        }
                    }
                    if (temp2 == 0)
                        votes = votes + "N/A\n";

                    if (i != selectedPosition.size() - 1)
                        votes = votes + "\n";
                }

                String preview = votes;
                int selected = temp;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (selected > 0) {
                            new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Preview")
                                    .setMessage(preview)
                                    .setPositiveButton("Submit", (dialog, which) -> {
                                        validateVotes();
                                    })
                                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            setError("Please select a candidate.");
                        }
                    }
                });
            }
        });
    }

    private void setLoading(boolean load) {
        if (load) {
            loadingLayout.setVisibility(View.VISIBLE);
            btnPreview.setEnabled(false);
            btnSubmit.setEnabled(false);
            rvPositions.setEnabled(false);
            btnViewResult.setEnabled(false);
            return;
        }
        loadingLayout.setVisibility(View.GONE);
        btnPreview.setEnabled(true);
        btnSubmit.setEnabled(true);
        rvPositions.setEnabled(true);
        btnViewResult.setEnabled(true);
    }

    private void setError(String message) {
        if (message.isEmpty()) {
            txtErrorDescription.setVisibility(View.GONE);
            txtErrorDescription.setText("");
            return;
        }
        txtErrorDescription.setText(message);
        txtErrorDescription.setVisibility(View.VISIBLE);
    }

    private void validateVotes() {
        setLoading(true);
        setError("");

        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // do in background
                ArrayList<Candidate> selectedCandidate = positionAdapter.canData;
                ArrayList<Position> selectedPosition = positionAdapter.data;

                for (int i = 0; i < selectedPosition.size(); i++) {
                    String position = selectedPosition.get(i).getName();
                    int maxVote = selectedPosition.get(i).getMaxVote();
                    int currentVote = 0;

                    for (int j = 0; j < selectedCandidate.size(); j++) {
                        if (selectedCandidate.get(j).getPositionID() == selectedPosition.get(i).getId()) {
                            if (selectedCandidate.get(j).isSelected()) {
                                currentVote++;
                            }
                        }
                    }

                    if (currentVote != maxVote) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (maxVote > 1) {
                                    setError("Please select " +
                                            maxVote + " candidates for " +
                                            position + ".");
                                } else {
                                    setError("Please select a candidate for " +
                                            position + ".");
                                }

                                setLoading(false);
                            }
                        });

                        break;
                    }

                    if (i == selectedPosition.size() - 1) {

                        try {
                            if (voteManager.submitVote(selectedCandidate, selectedPosition)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Vote successfully submitted.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.d("validateVotes()", "Vote Submitted");
                            }
                        } catch (SQLException e) {
                            Log.e("validateVotes()", e.getMessage());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadPositions();
                            }
                        });
                    }
                }
            }
        });
    }

    private class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.ViewHolder> {

        private Context context;
        private ArrayList<Position> data;
        private ArrayList<Candidate> canData;

        public PositionsAdapter(Context context, ArrayList<Position> data, ArrayList<Candidate> canData) {
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
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PositionsAdapter.ViewHolder holder, int position) {
            initialize(holder, position);

        }

        // code here
        private void initialize(ViewHolder holder, int position) {
            View view = holder.itemView;
            Position positionData = data.get(position);

            final TextView txtPosition = view.findViewById(R.id.txtPosition);
            final TextView txtDescription = view.findViewById(R.id.txtDescription);
            final RecyclerView rvCandidates = view.findViewById(R.id.rvCandidates);
            final CardView main_card = view.findViewById(R.id.main_card);
            final LinearLayout sepTop = view.findViewById(R.id.seperateTop);
            final LinearLayout sepBottom = view.findViewById(R.id.seperateBottom);

            sepTop.setVisibility(View.GONE);
            if (position == 0)
                sepTop.setVisibility(View.VISIBLE);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

            txtPosition.setText(positionData.getName());

            int max_vote = positionData.getMaxVote();
            if (max_vote > 1)
                txtDescription.setText("Select " + max_vote + " candidates.");
            else
                txtDescription.setText("Select only one candidate.");


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

                    ArrayList<Candidate> newCand = new ArrayList<>();
                    for (int i = 0; i < canData.size(); i++) {
                        if (canData.get(i).getPositionID() == positionData.getId()) {
                            newCand.add(canData.get(i));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            /*if (position+1 == data.size())
                                setLoading(false);*/

                            rvCandidates.setAdapter(new CandidatesAdapter(context, newCand, max_vote));
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

            private ArrayList<Candidate> data;
            private int currentVote = 0;
            private int maxVote;
            private Context context;

            public CandidatesAdapter(Context context, ArrayList<Candidate> data, int maxVote) {
                this.data = data;
                this.maxVote = maxVote;
                this.context = context;
            }

            @NonNull
            @Override
            public CandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.layout_candidate, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(lp);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull CandidatesAdapter.ViewHolder holder, int position) {
                initialize(holder, position);

            }

            // code here
            private void initialize(CandidatesAdapter.ViewHolder holder, int position) {
                View view = holder.itemView;
                Candidate candidate = data.get(position);

                final LinearLayout main_layout = view.findViewById(R.id.main_layout);
                final CheckBox chckbxVote = view.findViewById(R.id.chckbxVote);
                final TextView txtName = view.findViewById(R.id.txtName);
                final TextView txtProgram = view.findViewById(R.id.txtProgram);

                txtName.setText(candidate.getName());
                txtProgram.setText(candidate.getPartylist() + " - " + candidate.getProgram());

                if (candidate.isSelected())
                    chckbxVote.setChecked(candidate.isSelected());


                main_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chckbxVote.isChecked())
                            chckbxVote.setChecked(false);
                        else if (currentVote < maxVote)
                            chckbxVote.setChecked(true);

                        candidate.setSelected(chckbxVote.isChecked());
                    }
                });

                chckbxVote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            currentVote++;
                            setError("");
                        } else {
                            currentVote--;
                        }
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

}