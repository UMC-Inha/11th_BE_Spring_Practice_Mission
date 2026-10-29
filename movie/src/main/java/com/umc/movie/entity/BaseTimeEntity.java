package com.umc.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
//이 클래스는 그 자체로 테이블이 되지는 않지만, 상속하는 자식 엔티티에게 칼럼 정의를 물려준다.
@MappedSuperclass
public abstract class BaseTimeEntity {

    //처음 저장될 때 HIbernate가 자동으로 채워줌.
    //이후에는 절대 바뀌지 않음
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

}

