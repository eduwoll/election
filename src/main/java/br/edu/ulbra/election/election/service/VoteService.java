package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.model.Voter;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.client.CandidateClientService;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;

    private final VoterClientService voterClientService;

    private final CandidateClientService candidateClientService;


    private final ModelMapper modelMapper;

    @Autowired
    public VoteService(VoteRepository voteRepository, ModelMapper modelMapper, ElectionRepository electionRepository, VoterClientService voterClientService, CandidateClientService candidateClientService){
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.voterClientService = voterClientService;
        this.candidateClientService = candidateClientService;
        this.modelMapper = modelMapper;
    }

    public GenericOutput electionVote(VoteInput voteInput){

        Election election = validateInput(voteInput.getCandidateNumber(),voteInput.getElectionId(), voteInput,voteInput.getVoterId());
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
        }

        // TODO: Validate null candidate
        vote.setNullVote(false);

        voteRepository.save(vote);

        return new GenericOutput("OK");
    }

    public GenericOutput multiple(List<VoteInput> voteInputList){
        for (VoteInput voteInput : voteInputList){
            this.electionVote(voteInput);
        }
        return new GenericOutput("OK");
    }



    public Election validateInput(Long candidateNumber,Long electionId, VoteInput voteInput,Long voterId){



        if (voteInput.getCandidateNumber() == null){
            throw new GenericOutputException("Invalid Candidate");
        }
        else{
            try{
                CandidateOutput candidateOutput = candidateClientService.getByNumber(candidateNumber);
                voteInput.setCandidateNumber(candidateNumber);
            } catch (FeignException e){
                if (e.status() == 500) {
                    throw new GenericOutputException("Invalid Candidate");
                }
            }
        }

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException("Invalid Election");
        }

        if (voteInput.getVoterId() == null){
            throw new GenericOutputException("Invalid Voter");
        }
        else{
            try{
                VoterOutput voterOutput = voterClientService.getById(voterId);
                voteInput.setVoterId(voterId);
            } catch (FeignException e){
                if (e.status() == 500) {
                    throw new GenericOutputException("Invalid Voter");
                }
            }
        }

        return election;
    }
}
