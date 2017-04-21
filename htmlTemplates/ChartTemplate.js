Highcharts.chart('<!-- CONTAINER_SEARCH_TOKEN -->', {
    chart: {
        type: 'spline'
    },
    title: {
        text: ''
    },
    subtitle: {
        text: '<!-- TITLE_SEARCH_TOKEN -->'
    },
    xAxis: {
        type: 'datetime',
        dateTimeLabelFormats: { // don't display the dummy year
            month: '%e. %b',
            year: '%b'
        },
        title: {
            text: 'Date'
        }
    },
    yAxis: {
        title: {
            text: 'Price'
        },
        min: 0
    },
    tooltip: {
        headerFormat: '<b>{series.name}</b><br>',
        pointFormat: '{point.x:%e. %b}: {point.y:} taadaa'
    },

    plotOptions: {
        spline: {
            marker: {
                enabled: true
            }
        }
    },

    series: [{
        name: '<!-- TITLE_SEARCH_TOKEN -->',
        // Define the data points. All series have a dummy year
        // of 1970/71 in order to be compared on the same x axis. Note
        // that in JavaScript, months start at 0 for January, 1 for February etc.
        data: [
<!-- DATA_SET_SEARCH_TOKEN -->
        ]
    }]
});