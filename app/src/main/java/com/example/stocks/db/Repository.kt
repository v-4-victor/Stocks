/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.stocks.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Repository(private val database: CompanyDB) {
    private val dao = database.companyDao
    suspend fun Insert(set: DBCompanyInfo) {
        withContext(Dispatchers.IO) {
            dao.insert(set)
        }
    }
    suspend fun Delete(set: String) {
        withContext(Dispatchers.IO) {
            dao.delete(set)
        }
    }
    suspend fun updateTime(newTime:Long, symbols:List<String>){
        withContext(Dispatchers.IO) {
            symbols.forEach { dao.updateTime(newTime, it)}
        }
    }
    suspend fun updatePrice(openPrice:Double, currentPrice:Double, symbol:String){
        withContext(Dispatchers.IO) {
            dao.updatePrice(openPrice,currentPrice, symbol)
        }
    }
    suspend fun updateFavourite(symbol:String, favourite:Boolean){
        withContext(Dispatchers.IO) {
            dao.updateFavourite(symbol, favourite)
        }
    }
    fun getStaticCompInfo() = dao.getStaticCompInfo()
    fun getCompanies() = dao.getCompanies()
    fun getLiveCompanies() = dao.getLiveCompanies()

}
