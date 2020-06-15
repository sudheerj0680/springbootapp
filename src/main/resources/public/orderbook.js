angular.module('orderbookmanagement', [])
.controller('OrderbookController', ['$scope','$http','$location',function($scope, $http, $location) {
    API_URL=$location.absUrl();
    $scope.instruments = {};
    $scope.msg = "";
    $( "#entryDatepicker" ).datepicker({
             format: 'yyyy-mm-dd'
    });
    $scope.fetchInstruments = function() {
        $http.get(API_URL+'/orderbook/fetchInstruments').
        then(function(response) {
            $scope.instruments = response.data;
    })};

    $scope.fetchOrderBooks = function() {
        $http.get(API_URL+'/orderbook/get-all').
            then(function(response) {
                $scope.orderbooks = response.data;
                //console.log($scope.orderbooks);
            })
     };

    $scope.openOrderBook = function(instrument) {
        var id = instrument.id;
        //console.log("instrumentId="+ id);
        $http.post(API_URL+'/orderbook/'+id +'/open')
        .then(function successCallback(response) {
                $scope.showAlert(response.data.successMessage);
                $scope.fetchOrderBooks();
        }
        , function errorCallback(response){
                //$scope.msg = { message: error, status: status};
                console.log(response);
        });
    };

    $scope.closeOrderBook = function(instrument) {
            var id = instrument.id;
            //console.log("instrumentId="+ id);
            $http.put(API_URL+'/orderbook/'+id+'/close')
            .then(function successCallback(response) {
                    $scope.showAlert(response.data.successMessage);
                    $scope.fetchOrderBooks();
            }
            , function errorCallback(response){
                    //$scope.msg = { message: error, status: status};
                    console.log(response);
            });
        };

    $scope.executeOrderBook = function() {
        var orderBookId = $("#execOrderBookIdInput").val();
        var data = {
                quantity: $("#executionQuantityInput").val(),
                executionPrice: $("#executionPriceInput").val()
        };
        $http.put(API_URL+'/orderbook/'+orderBookId+'/execute', JSON.stringify(data))
        .then(function successCallback(response) {
                $scope.showAlert(response.data.successMessage);
                $scope.fetchOrderBooks();
        }
        , function errorCallback(response){
                //$scope.msg = { message: error, status: status};
                console.log(response);
        });
        $scope.resetExecuteModal();
    };

    $scope.addOrder = function() {
    var orderBookId = $("#orderBookIdInput").val();
    var entryDate = $("#entryDatepicker").val();
    var data = {
        instrumentId: $("#instrumentIdInput").val(),
        quantity: $("#quantityInput").val(),
        price: $("#priceInput").val(),
        entryDate: entryDate,
        orderType: $("#selectOrderType").val()
    };
         $http.put(API_URL+'/orderbook/'+orderBookId+'/order',JSON.stringify(data))
         .then(function successCallback(response) {
                 $scope.showAlert(response.data.successMessage);
                 $scope.fetchOrderBooks();
         }
         , function errorCallback(response){
                 //$scope.msg = { message: error, status: status};
                 console.log(response);
         });
         $scope.resetAddOrderModal();
    };

    $scope.checkStatus = function(status, instrument) {
        var statusArray = status.split(",");
        var flag = true;
        $.each(statusArray, function(index, value) {
            if(value == instrument.status) {
                flag = false;
                return false; // breaks
            }
        });
        return flag;
    }

    $('#closeAlertBtn').on('click', function() {
      $("#msgs").hide();
    });

    $scope.showAlert = function(msg) {
        $("#msg").html(msg);
        $("#msgs").show();
    }

    $scope.enableOpenOrderBtn = function(){
        if($scope.instrumentModel == null) {
            $("#openBookBtn").prop('disabled', true);
        } else {
            $("#openBookBtn").prop('disabled', false);
        }
    }

    $scope.openAddOrderModal = function(instrument) {
        $("#instrumentInput").val(instrument.instrument);
        $("#instrumentIdInput").val(instrument.instrumentId);
        $("#orderBookIdInput").val(instrument.id);
        $('#orderModal').modal({
        	show : true
        });
    }

    $scope.openExecuteModal = function(instrument) {
        $("#execOrderBookIdInput").val(instrument.id);
        $('#executeModal').modal({
            show : true
         });
    }

    $scope.showOrders = function(orders) {
         $scope.orders = orders;
         $('#showOrdersModal').modal({
            	show : true
         });
    }

    $scope.showExecutions = function(executions) {
             $scope.executions = executions;
             $('#showExecutionsModal').modal({
                	show : true
             });
        }

    $scope.showOrderBookStats = function(orderBook) {
          $http.get(API_URL+'/orderbook/'+orderBook.id+'/detail-stats')
          .then(function successCallback(response) {
                  $scope.orderBookStats = response.data;
                  $scope.orderBookStats['instrument'] = orderBook.instrument;
                  $scope.showAlert(response.data.successMessage);
                  $('#showOrderbookStatsModal').modal({
                      show : true
                  });
          }
          , function errorCallback(response){
                  //$scope.msg = { message: error, status: status};
                  console.log(response);
          });	show : true
    }

    $scope.resetAddOrderModal = function () {
        $scope.quantity='';
        $scope.price='';
        $scope.entryDate='';
        $("#orderModal .close").click();
    }

    $scope.resetExecuteModal = function () {
        $scope.executionQuantity='';
        $scope.executionPrice='';
        $("#executeModal .close").click();
    }

    $scope.fetchInstruments();
    $scope.fetchOrderBooks();
}]);