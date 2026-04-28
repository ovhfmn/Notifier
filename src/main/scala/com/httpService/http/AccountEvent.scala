package com.httpService.http

import io.circe.Codec

enum AccountEvent derives Codec.AsObject:
  case AccountCreated(id: String, initialBalance: BigDecimal)
  case MoneyDebited(id: String, amount: BigDecimal, newBalance: BigDecimal)
  case MoneyCredited(id: String, amount: BigDecimal, newBalance: BigDecimal)