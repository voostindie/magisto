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
    <style>
        body {
            padding-top: 20px;
            padding-bottom: 20px;
        }
        @media (min-width: 768px) {
            .container {
                max-width: 730px;
            }
        }
        blockquote {
            font-size: 1em;
        }
        footer {
            border-top: 1px solid #e5e5e5;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" class="active">
                <a href="#content" role="tab" data-toggle="tab">Content</a>
            </li>
            <li role="presentation">
                <a href="#history" role="tab" data-toggle="tab">History</a>
            </li>
        </ul>
    </header>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="content">
            <article>
            ${content}
            </article>
        </div>
        <div role="tabpanel" class="tab-pane" id="history">
            <h1>Page history</h1>
            <table class="table-condensed">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Changed by</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <#list history.commits as commit>
                <tr>
                    <td>${commit.timestamp?datetime}</td>
                    <td>${commit.committer}</td>
                    <td>${commit.shortMessage}</td>
                </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
    <footer>
        <p class="text-muted">
            Last changed by ${history.lastCommit.committer} on ${history.lastCommit.timestamp?date} at ${history.lastCommit.timestamp?time}.<br/>
            Generated on ${timestamp?date} at ${timestamp?time} from /${path}.
        </p>
    </footer>
</div>
<script>
    $("table").addClass("table table-striped");
    $("img").addClass("img-responsive img-rounded");
</script>
</body>
</html>
