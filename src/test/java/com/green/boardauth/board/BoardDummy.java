package com.green.boardauth.board;

import com.green.boardauth.Dummy;
import com.green.boardauth.application.board.BoardMapper;
import com.green.boardauth.application.board.model.BoardPostReq;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

public class BoardDummy extends Dummy {

    final int ADD_ROW_COUNT = 10_000;

    @Test
    void generate() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        BoardMapper boardMapper = sqlSession.getMapper(BoardMapper.class);

        for (int i = 0; i < ADD_ROW_COUNT; i++) {
            String title = koFaker.book().title();
            String content = koFaker.lorem().sentence();
            long userId = koFaker.random().nextInt(4, 1003);

            System.out.printf("title: %s\n", title);
            System.out.printf("content: %s\n", content);
            System.out.printf("userId: %s\n", userId);

            BoardPostReq req = new BoardPostReq();
            req.setTitle(title);
            req.setContents(content);
            req.setUserId(userId);

            boardMapper.postBoard(req);
        }
        sqlSession.flushStatements();
    }
}
