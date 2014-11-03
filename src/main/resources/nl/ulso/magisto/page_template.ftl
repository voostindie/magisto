<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css"/>
    <script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div>
        <ul class="nav nav-pills pull-right" role="tablist">
            <li role="presentation" class="active">
                <a href="#content" role="tab" data-toggle="tab">Content</a>
            </li>
            <li role="presentation">
                <a href="#history" role="tab" data-toggle="tab">History</a>
            </li>
        </ul>
    </div>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="content">
            <article>
            ${content}
            </article>
        </div>
        <div role="tabpanel" class="tab-pane" id="history">
            <table class="table-condensed">
                <thead>
                <tr>
                    <th>Commit</th>
                    <th>Timestamp</th>
                    <th>Short message</th>
                </tr>
                </thead>
                <tbody>
                <#list history.commits as commit>
                <tr>
                    <td><a href="#${commit.shortId}">${commit.shortId}</a></td>
                    <td>${commit.timestamp?date} at ${commit.timestamp?time}</td>
                    <td>${commit.shortMessage}</td>
                </tr>
                </#list>
                </tbody>
            </table>
            <#list history.commits as commit>
                <h2><a name="${commit.shortId}"></a><strong>${commit.shortId}</strong>: ${commit.shortMessage}</h2>
                <ul>
                    <li><strong>Commit</strong>: ${commit.id}</li>
                    <li><strong>Timestamp</strong>: ${commit.timestamp?date} at ${commit.timestamp?time}</li>
                    <li><strong>Committer</strong>: ${commit.committer}</li>
                </ul>
                <pre>${commit.fullMessage?html}</pre>
            </#list>
        </div>
        <p class="text-muted">
            Last changed on ${history.lastCommit.timestamp?date} at ${history.lastCommit.timestamp?time}
            by ${history.lastCommit.committer} in commit ${history.lastCommit.shortId}.<br/>
            Generated on ${timestamp?date} at ${timestamp?time} from /${path}.
        </p>
    </div>
</div>
<script>
    $("table").addClass("table table-striped");
    $("img").addClass("img-responsive img-rounded");
</script>
</body>
</html>
