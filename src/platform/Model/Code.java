package platform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code {
    @Id
    @JsonIgnore
    private String id;

    @JsonIgnore
    @UpdateTimestamp
    LocalDateTime dateTime;
    private String date;
    private String code;
    private long time;
    private long views;
    @JsonIgnore
    private boolean restrictTime;
    @JsonIgnore
    private boolean restrictView;
}
