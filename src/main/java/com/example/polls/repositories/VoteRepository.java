package com.example.polls.repositories;

import com.example.polls.models.ChoiceVoteCount;
import com.example.polls.models.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// All of the methods in VoteRepository have a custom query with @Query annotation. I’ve used custom queries because -
//
// Many of the queries cannot be constructed by Spring-Data-Jpa’s dynamic query methods.
// Even if they can be constructed, they don’t generate an optimized query.
// Note that, we’re using JPQL constructor expression in some of the queries to return the query result in the form of a custom class called ChoiceVoteCount.

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
	@Query("SELECT NEW com.example.polls.models.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
	List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);
	
	@Query("SELECT NEW com.example.polls.models.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
	List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);
	
	@Query("SELECT v FROM Vote v where v.user.id = :userId and v.poll.id in :pollIds")
	List<Vote> findByUserIdAndPollIdIn(@Param("userId") Long userId, @Param("pollIds") List<Long> pollIds);
	
	@Query("SELECT v FROM Vote v where v.user.id = :userId and v.poll.id = :pollId")
	Vote findByUserIdAndPollId(@Param("userId") Long userId, @Param("pollId") Long pollId);
	
	@Query("SELECT COUNT(v.id) from Vote v where v.user.id = :userId")
	long countByUserId(@Param("userId") Long userId);
	
	@Query("SELECT v.poll.id FROM Vote v WHERE v.user.id = :userId")
	Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
