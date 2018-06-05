<?php
$_POST = json_decode(file_get_contents('php://input'), true);
http_response_code(200);
header('Content-type: application/json');
if ($_SERVER['PATH_INFO'] === '/connect')
	echo json_encode("test.example@epitech.eu");
else if ($_SERVER['PATH_INFO'] === '/user') {
	$data = array(
		"email" => "test.example@epitech.eu",
		"name" => "mickael",
		"age" => 13,
		"password" => "kek",
		"score" => 3,
		"image" => "http://" . $_SERVER['HTTP_HOST'] . "/image.png",
		"admin" => true
	);
	echo json_encode($data);
}
else if ($_SERVER['PATH_INFO'] === '/new') {
	echo json_encode($_POST);
}
else if ($_SERVER['PATH_INFO'] === '/stat') {
	if (isset($_SERVER['QUERY_STRING']) && strpos($_SERVER['QUERY_STRING'], "email") !== false) {
		$data[] = array(
			"title" => "Calcul mental",
			"score" => 9,
			"questions" => 14,
			"timeAvg" => 4.4
		);
		$data[] = array(
			"title" => "Mot du jour",
			"score" => 2,
			"questions" => 10,
			"timeAvg" => 9.0
		);
		$data[] = array(
			"title" => "Association d'image",
			"score" => 1,
			"questions" => 5,
			"timeAvg" => 4.2
		);
		$data[] = array(
			"title" => "Phrase dans l'ordre",
			"score" => 4,
			"questions" => 10,
			"timeAvg" => 5.7
		);
		$data[] = array(
			"title" => "Phonétique",
			"score" => 8,
			"questions" => 20,
			"timeAvg" => 6.8
		);
		$data[] = array(
			"title" => "Grammaire",
			"score" => 0,
			"questions" => 0,
			"timeAvg" => 0
		);
		$data[] = array(
			"title" => "Conjugaison",
			"score" => 9,
			"questions" => 19,
			"timeAvg" => 3.1
		);		
		echo json_encode($data);
	} else {
		$data[] = array(
			"title" => "Calcul mental",
			"score" => 95,
			"questions" => 140,
			"timeAvg" => 5.3
		);
		$data[] = array(
			"title" => "Mot du jour",
			"score" => 80,
			"questions" => 100,
			"timeAvg" => 4.7
		);
		$data[] = array(
			"title" => "Association d'image",
			"score" => 56,
			"questions" => 90,
			"timeAvg" => 9.3
		);
		$data[] = array(
			"title" => "Phrase dans l'ordre",
			"score" => 767,
			"questions" => 998,
			"timeAvg" => 4.9
		);
		$data[] = array(
			"title" => "Phonétique",
			"score" => 78,
			"questions" => 99,
			"timeAvg" => 2.2
		);
		$data[] = array(
			"title" => "Grammaire",
			"score" => 87,
			"questions" => 765,
			"timeAvg" => 6.5
		);
		$data[] = array(
			"title" => "Conjugaison",
			"score" => 90,
			"questions" => 190,
			"timeAvg" => 8.4
		);
		echo json_encode($data);
	}
}
else if ($_SERVER['PATH_INFO'] === '/save_score') {
	echo json_encode("Saved");
}
else if ($_SERVER['PATH_INFO'] === '/save_image') {
	$base64 = $_POST['image'];


$imageData = base64_decode($base64);
$source = imagecreatefromstring($imageData);
$imageSave = imagejpeg($source,'image.png',100);
imagedestroy($source);
		$data = array(
		"email" => "test.example@epitech.eu",
		"name" => "mickael",
		"age" => 13,
		"password" => "kek",
		"score1" => 3,
		"image" => "http://" . $_SERVER['HTTP_HOST'] . "/image.png"
	);
	echo json_encode($data);
}
else if ($_SERVER['PATH_INFO'] === '/questions/Calcul mental/1') {
	if ($_SERVER['REQUEST_METHOD'] === 'GET') {
		$data[] = array(
			"question" => "http://" . $_SERVER['HTTP_HOST'] . "/image.png",
			"image" => true,
			"rep1" => "rep1",
			"rep2" => "rep2",
			"rep3" => "rep3",
			"rep4" => "rep4",
			"correction" => 1
		);
		echo json_encode($data);
	} else {
		echo json_encode("ok");
	}
}
else// new
	echo json_encode("hi");

// see data
$stdout = fopen('php://stdout', 'w');
ob_start();
var_dump($_POST);
$data = ob_get_clean();
fwrite($stdout, $data."\n");


function saveImage($img) {
    header('Content-Type: bitmap; charset=utf-8');
    $file = fopen('uploaded_image.jpg', 'wb');
    fwrite($file, $img);
    fclose($file);
}
?>
