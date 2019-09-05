package com.meetball.demo.persistence;

import com.meetball.demo.domain.Match;
import com.meetball.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    public User getUserByUserName(@Param("userName")String userName);

    public void insertUser(User user);

    public void setImage(@Param("userName")String userName,@Param("imageNum")int imageNum);

    public int getImageNum(@Param("userName")String userName);

    //public List<Match> getAllMatch(@Param("method")int method);

    //public void updateMatch(Match match);

    //public void deleteById(Match match);

    //public void insertMatch(Match match);

    //public void updateUser(User user);

    //public Match getMatchInfo(@Param("matchID") int matchID);

}
