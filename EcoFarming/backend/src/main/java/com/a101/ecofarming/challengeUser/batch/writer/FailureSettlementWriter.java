package com.a101.ecofarming.challengeUser.batch.writer;

import com.a101.ecofarming.challengeUser.dao.ChallengeUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailureSettlementWriter implements ItemWriter<ChallengeUserDao> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends ChallengeUserDao> chunk) {
        chunk.forEach(user -> {
            // 실패한 사용자의 return_amount 업데이트
            jdbcTemplate.update("UPDATE challenge_user SET return_amount = ? WHERE challenge_user_id = ?",
                    user.getReturnAmount(), user.getChallengeUserId());

            // 실패한 사용자의 amount 업데이트
            jdbcTemplate.update("UPDATE user SET amount = amount + ? WHERE user_id = ?",
                    user.getReturnAmount(), user.getUserId());
        });
    }
}
