package cool.houge.mahu.common.rsql;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Bean {

    private String genres;
    private String director;
    private String actor;
    private Integer year;
}
