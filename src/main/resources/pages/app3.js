/**
 * Created by nissa on 12/17/2017.
 */

<!-----------counter ,source=https://stackoverflow.com/questions/1535631/static-variables-in-javascript--------------------------->
var incrTable = (function () {
    var i = 1;

    return function () {
        return i++;
    }
})();
<!-----------end of counter --------------------------->

<!-----------start tooltips nissan--------------------------->
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});
<!--------------end of tooltips--------------------------->

$("button#submit").click(() => {
    var mac  = $("input#mac").val();
var name   = $("input#mac").val();
alert(mac)
alert(name)
$.ajax(
    {
        "url": encodeURI("/app/submitAlgo1?" + mac + "," + name)
    }
).then(
    function(output) {
        var splitOutput = output.split(",");

        new Chart(document.getElementById("myChart"),
            {"type":"bubble","data":{"datasets":[{"label":"Wifi Points",
                "data":[{"x":31,"y":30,"r":5},{"x":parseFloat(splitOutput[0]),"y":parseFloat(splitOutput[1]),"r":10},{"x":30,"y":33,"r":5},{"x":33,"y":36,"r":5}],
                "backgroundColor":"rgb(255, 99, 132)"}]}});


        <!--------------append wifi point to table--------------------------->
        var tableColor;
        var iterationTable= incrTable();
        if (iterationTable%2==1){
            tableColor='danger';
        }else {
            tableColor ='info';
        }
        var tr = "<tr class="+tableColor+"><td>"+splitOutput[3]+"</td><td>"
            +splitOutput[0]+"</td><td>"+splitOutput[1]+"</td><td>"+splitOutput[2]+"</td></tr>"
        $('table#wifiTable').append(tr);
        <!--------------end of appending table--------------------------->
    }
);
return false
})

<!--end of Algo 1-->

<!--start of save 2 csv-->

$("button#save2csvbutton").click(() => {
    alert("will save to csv")
$.ajax(
    {
        "url": encodeURI("/app/save2csv")
    }
).then(
    function(output) {
        var n = output.includes("true");
        $('div#filePopup').show();
        if(n==true)
            $("h4#filePopupMsg").html("saved")
        else
            $("h4#filePopupMsg").html("Can't save no files, try loading some files dimwit.")
    }
);
return false
})

<!--end of save 2 csv-->

<!-------------------start of algo 2-------------------------->

$("button#submitAlgo2").click(() => {
    var mac1Algo2  = $("inputmac1Algo2").val();
var mac2Algo2   = $("input#mac1Algo2").val();
var mac3Algo2  = $("inputmac3Algo2").val();
var sig1Algo2   = $("input#sig1Algo2").val();
var sig2Algo2   = $("input#sig2Algo2").val();
var sig3Algo2   = $("input#sig3Algo2").val();


$.ajax(
    {
        "url": encodeURI("/app/submitAlgo2?" + mac1Algo2 + "," + mac2Algo2, +","+mac3Algo2 +","+sig1Algo2 +","+sig2Algo2 +","+sig3Algo2)
    }
).then(
    function(output) {
        var splitOutput = output.split(",");

        new Chart(document.getElementById("myChartAlgo2"),
            {"type":"bubble","data":{"datasets":[{"label":"Wifi Points",
                "data":[{"x":31,"y":30,"r":5},{"x":parseFloat(splitOutput[0]),"y":parseFloat(splitOutput[1]),"r":10},{"x":30,"y":33,"r":5},{"x":33,"y":36,"r":5}],
                "backgroundColor":"rgb(255, 99, 132)"}]}});


        <!--------------append wifi point to table--------------------------->
        var tableColor;
        var iterationTable= incrTable();
        if (iterationTable%2==1){
            tableColor='danger';
        }else {
            tableColor ='info';
        }
        var tr = "<tr class="+tableColor+"><td>"+splitOutput[3]+"</td><td>"
            +splitOutput[0]+"</td><td>"+splitOutput[1]+"</td><td>"+splitOutput[2]+"</td></tr>"
        $('table#wifiTable').append(tr);
        <!--------------end of appending table--------------------------->
    }
);
return false
})

<!-------------------end of algo 2-------------------------->

<!-------------------------scroll animation--------------------------->
$(document).on('click', 'a[href^="#"]', function (event) {
    event.preventDefault();

    $('html, body').animate({
        scrollTop: $($.attr(this, 'href')).offset().top
    }, 500);
});

