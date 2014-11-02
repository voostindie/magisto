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
    <article>
        ${content}
    </article>
    <p class="text-muted">
        Last changed on ${commit.timestamp?date} at ${commit.timestamp?time} by ${commit.committer} in commit ${commit.shortId}.<br/>
        Generated on ${timestamp?date} at ${timestamp?time} from /${path}.
    </p>
</div>
<script>
    $("table").addClass("table table-striped");
    $("img").addClass("img-responsive img-rounded");
</script>
</body>
</html>
