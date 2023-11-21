/*
 * Selling Partner API for Notifications
 * The Selling Partner API for Notifications lets you subscribe to notifications that are relevant to a selling partner's business. Using this API you can create a destination to receive notifications, subscribe to notifications, delete notification subscriptions, and more.  For more information, see the [Notifications Use Case Guide](doc:notifications-api-v1-use-case-guide).
 *
 * OpenAPI spec version: v1
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 *
 * Swagger Codegen version: 2.4.9
 *
 * Do not edit the class manually.
 *
 */

import { ApiClient } from "../ApiClient.js";
import { OrderChangeTypes } from "./OrderChangeTypes.js";

/**
 * The OrderChangeTypeFilter model module.
 * @module notifications/js-client.notifications.model/OrderChangeTypeFilter
 * @version v1
 */
export class OrderChangeTypeFilter {
  /**
   * Constructs a new <code>OrderChangeTypeFilter</code>.
   * Use this event filter to customize your subscription to send notifications for only the specified orderChangeType.
   * @alias module:notifications/js-client.notifications.model/OrderChangeTypeFilter
   * @class
   */
  constructor() {}

  /**
   * Constructs a <code>OrderChangeTypeFilter</code> from a plain JavaScript object, optionally creating a new instance.
   * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
   * @param {Object} data The plain JavaScript object bearing properties of interest.
   * @param {module:notifications/js-client.notifications.model/OrderChangeTypeFilter} obj Optional instance to populate.
   * @return {module:notifications/js-client.notifications.model/OrderChangeTypeFilter} The populated <code>OrderChangeTypeFilter</code> instance.
   */
  static constructFromObject(data, obj) {
    if (data) {
      obj = obj || new OrderChangeTypeFilter();
      if (data.hasOwnProperty("orderChangeTypes"))
        obj.orderChangeTypes = OrderChangeTypes.constructFromObject(
          data["orderChangeTypes"],
        );
    }
    return obj;
  }
}

/**
 * @member {module:notifications/js-client.notifications.model/OrderChangeTypes} orderChangeTypes
 */
OrderChangeTypeFilter.prototype.orderChangeTypes = undefined;
