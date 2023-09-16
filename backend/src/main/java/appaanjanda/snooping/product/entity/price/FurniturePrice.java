package appaanjanda.snooping.product.entity.price;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.List;

@Document(indexName = "가구")
@Getter
@Setter
public class FurniturePrice {

    @Id
    private String id;

    @Field(name = "routing", type = FieldType.Text)
    private String routing;

    @Field(name = "price_history", type = FieldType.Nested)
    private List<PriceHistory> priceHistory;
}
