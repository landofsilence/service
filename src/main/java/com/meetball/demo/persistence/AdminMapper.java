package com.meetball.demo.persistence;

import com.meetball.demo.domain.Admin;
import com.meetball.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    public Admin login(Admin admin);

    //public List<Match> getAllMatch(@Param("method")int method);

    //public void updateMatch(Match match);

    //public void deleteById(Match match);

    //public void insertMatch(Match match);

    //public void updateUser(User user);

    //public Match getMatchInfo(@Param("matchID") int matchID);

}
