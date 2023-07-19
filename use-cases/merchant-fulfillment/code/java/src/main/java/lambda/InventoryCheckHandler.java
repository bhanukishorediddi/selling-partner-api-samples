package lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.swagger.client.model.mfn.PackageDimensions;
import io.swagger.client.model.mfn.UnitOfLength;
import io.swagger.client.model.mfn.UnitOfWeight;
import io.swagger.client.model.mfn.Weight;
import lambda.utils.MfnOrder;
import lambda.utils.MfnOrderItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;
import static lambda.utils.Constants.INVENTORY_TABLE_HASH_KEY_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_HEIGTH_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_LENGTH_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_NAME_ENV_VARIABLE;
import static lambda.utils.Constants.INVENTORY_TABLE_SIZE_UNIT_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_STOCK_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_WEIGHT_UNIT_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_WEIGHT_VALUE_ATTRIBUTE_NAME;
import static lambda.utils.Constants.INVENTORY_TABLE_WIDTH_ATTRIBUTE_NAME;

public class InventoryCheckHandler implements RequestHandler<MfnOrder, MfnOrder> {

    public MfnOrder handleRequest(MfnOrder input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("InventoryCheck Lambda input: " + input);

        long packageWeightValue = 0;
        String packageWeightUnit = "";

        long packageLength = 0;
        long packageWidth = 0;
        long packageHeight = 0;
        String packageSizeUnit = "";

        //Iterate over all order items and retrieve stock, size and weight from the database
        for (MfnOrderItem orderItem : input.getOrderItems()) {
            //Retrieve the item from DynamoDB by SKU
            //Update this section to match your product's logic
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(INVENTORY_TABLE_HASH_KEY_NAME, new AttributeValue(orderItem.getSku()));

            GetItemRequest getItemRequest = new GetItemRequest()
                    .withTableName(System.getenv(INVENTORY_TABLE_NAME_ENV_VARIABLE))
                    .withKey(key);

            AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
            GetItemResult getItemResult = dynamoDB.getItem(getItemRequest);
            Map<String, AttributeValue> item = getItemResult.getItem();

            String stock = item.get(INVENTORY_TABLE_STOCK_ATTRIBUTE_NAME).getN();
            if (parseLong(stock) < orderItem.getQuantity()) {
                throw new InternalError(
                        String.format("Stock level for SKU {%s} is not enough to fulfill the requested quantity",
                        orderItem.getSku()));
            }

            long itemWeightValue = parseLong(item.get(INVENTORY_TABLE_WEIGHT_VALUE_ATTRIBUTE_NAME).getN());
            //Valid values for the database records are uppercase: [OZ, G]
            String itemWeightUnit = item.get(INVENTORY_TABLE_WEIGHT_UNIT_ATTRIBUTE_NAME).getS();

            long itemLength = parseLong(item.get(INVENTORY_TABLE_LENGTH_ATTRIBUTE_NAME).getN());
            long itemWidth = parseLong(item.get(INVENTORY_TABLE_WIDTH_ATTRIBUTE_NAME).getN());
            long itemHeight = parseLong(item.get(INVENTORY_TABLE_HEIGTH_ATTRIBUTE_NAME).getN());
            //Valid values for the database records are uppercase: [INCHES, CENTIMETERS]
            String itemSizeUnit = item.get(INVENTORY_TABLE_SIZE_UNIT_ATTRIBUTE_NAME).getS();

            Weight itemWeight = new Weight();
            itemWeight.setValue(BigDecimal.valueOf(itemWeightValue));
            itemWeight.setUnit(UnitOfWeight.valueOf(itemWeightUnit));
            orderItem.setItemWeight(itemWeight);

            //Package weight is calculated by adding the individual weights
            //Update this section to match your selling partners' logic
            packageWeightValue += itemWeightValue;
            packageWeightUnit = itemWeightUnit;

            //Package size is calculated by adding the individual sizes
            //Update this section to match your selling partners' logic
            packageLength += itemLength;
            packageWidth += itemWidth;
            packageHeight += itemHeight;
            packageSizeUnit = itemSizeUnit;
        }

        input.setWeight(new Weight()
                .value(BigDecimal.valueOf(packageWeightValue))
                .unit(UnitOfWeight.valueOf(packageWeightUnit)));

        input.setPackageDimensions(new PackageDimensions()
                .length(BigDecimal.valueOf(packageLength))
                .width(BigDecimal.valueOf(packageWidth))
                .height(BigDecimal.valueOf(packageHeight))
                .unit(UnitOfLength.valueOf(packageSizeUnit)));

        return input;
    }
}
