<?php

declare(strict_types=1);

namespace Database\Seeders;

use App\Enums\ContentStatus;
use App\Models\Media;
use App\Models\Menu;
use App\Models\Page;
use App\Models\Post;
use App\Models\PostCategory;
use Illuminate\Database\Seeder;

class CmsDemoSeeder extends Seeder
{
    public function run(): void
    {
        $heroMedia=Media::query()->updateOrCreate(['name'=>'Project Meridian — key art placeholder'],['disk'=>'external','url'=>null,'alt_text'=>'Abstrakcyjna grafika promocyjna gry Project Meridian','mime_type'=>'image/placeholder']);
        $home=Page::query()->updateOrCreate(['slug'=>'home'],['title'=>'Game Studio Platform','status'=>ContentStatus::Published,'template'=>'home','excerpt'=>'Odkrywaj wyjątkowe światy i poznaj gry tworzone z niezależnymi studiami.','seo_title'=>'Game Studio Platform — niezależne gry i wydawnictwo','seo_description'=>'Odkrywaj gry, śledź premiery i poznaj proces współpracy wydawniczej z Game Studio Platform.','published_at'=>now()->subDay()]);
        $home->blocks()->delete();
        $home->blocks()->createMany([
            ['type'=>'hero','position'=>10,'payload'=>['eyebrow'=>'Wyróżniona gra · narracyjne RPG','title'=>'Project Meridian','promise'=>'Wyrusz poza granice znanych map i zdecyduj, co naprawdę warto ocalić.','cta_label'=>'Odkryj Project Meridian','cta_url'=>'#worlds','release'=>'Premiera 2027','platforms'=>['PC','PlayStation 5','Xbox Series']]],
            ['type'=>'worlds','position'=>20,'payload'=>['eyebrow'=>'Znajdź swój następny świat','title'=>'Gry o różnych rytmach, jednej jakości','genres'=>['Wszystkie','RPG','Strategia','Horror','Logiczne'],'games'=>[
                ['title'=>'Project Meridian','genre'=>'RPG','status'=>'2027','tone'=>'mint'],['title'=>'Iron Vale','genre'=>'Strategia','status'=>'Early access','tone'=>'amber'],['title'=>'Stillwater Signal','genre'=>'Horror','status'=>'Wkrótce','tone'=>'violet'],['title'=>'Lumen Fold','genre'=>'Logiczne','status'=>'Dostępna','tone'=>'blue'],['title'=>'Ashline','genre'=>'Action RPG','status'=>'2028','tone'=>'red'],['title'=>'Moss & Machines','genre'=>'Cozy strategy','status'=>'W produkcji','tone'=>'green']]]],
            ['type'=>'releases','position'=>30,'payload'=>['eyebrow'=>'Premiery i produkcja','title'=>'Co nadchodzi','items'=>[
                ['date'=>'Q4 2026','title'=>'Lumen Fold','copy'=>'Premiera pełnej wersji na PC.'],['date'=>'2027','title'=>'Project Meridian','copy'=>'Narracyjne RPG science-fiction.'],['date'=>'2027','title'=>'Stillwater Signal','copy'=>'Psychologiczny horror skupiony na eksploracji.']]]],
            ['type'=>'publishing','position'=>40,'payload'=>['eyebrow'=>'Dla twórców','title'=>'Wydaj z nami bez utraty własnego głosu','copy'=>'Łączymy produkcję, marketing, dystrybucję i wsparcie wydawnicze z przejrzystym procesem decyzyjnym.','cta_label'=>'Poznaj proces wydawniczy','cta_url'=>'/wydaj-z-nami','steps'=>[
                ['number'=>'01','title'=>'Zgłoszenie','copy'=>'Poznajemy projekt, zespół i jego potrzeby.'],['number'=>'02','title'=>'Ewaluacja','copy'=>'Analizujemy potencjał, ryzyka i plan produkcji.'],['number'=>'03','title'=>'Współpraca','copy'=>'Ustalamy zakres wsparcia i wspólne kamienie milowe.'],['number'=>'04','title'=>'Premiera','copy'=>'Prowadzimy wydanie i dalszy rozwój gry.']]]],
            ['type'=>'community','position'=>50,'payload'=>['title'=>'Bądź bliżej premier','copy'=>'Newsletter, devlog i społeczność Discord w jednym miejscu. Zapisy e-mail uruchomimy wraz z modułem komunikacji transakcyjnej.','discord_label'=>'Dołącz do Discorda','discord_url'=>'#community']],
        ]);
        foreach([
            ['slug'=>'wydaj-z-nami','title'=>'Wydaj z nami','excerpt'=>'Przejrzysty proces wydawniczy od pierwszego zgłoszenia do premiery.','body'=>"Budujemy współpracę wokół konkretnego planu: zgłoszenie, screening, ewaluacja, rozmowa, oferta, umowa, produkcja i premiera.\n\nSzukamy zespołów z jasną wizją, realistycznym zakresem i gotowością do otwartej komunikacji."],
            ['slug'=>'o-nas','title'=>'O studiu','excerpt'=>'Tworzymy przestrzeń dla różnych gatunków i różnych zespołów.','body'=>"Game Studio Platform łączy studio, wydawnictwo i sklep w jednym spójnym ekosystemie.\n\nWspólny interfejs ma być neutralnym tłem dla własnej tożsamości każdej gry."],
            ['slug'=>'polityka-prywatnosci','title'=>'Polityka prywatności','excerpt'=>'Strona prawna zarządzana z CMS.','body'=>'To demonstracyjna treść Etapu 2. Finalne dokumenty prawne zostaną uzupełnione przed premierą produkcyjną.'],
        ] as $data){ Page::query()->updateOrCreate(['slug'=>$data['slug']],$data+['status'=>ContentStatus::Published,'template'=>'default','seo_title'=>$data['title'].' | Game Studio Platform','seo_description'=>$data['excerpt'],'published_at'=>now()->subDay()]); }
        $categories=collect([
            ['name'=>'Studio','slug'=>'studio'],['name'=>'Aktualizacja gry','slug'=>'aktualizacja-gry'],['name'=>'Premiera','slug'=>'premiera'],['name'=>'Wydarzenie','slug'=>'wydarzenie'],['name'=>'Artykuł techniczny','slug'=>'artykul-techniczny']
        ])->mapWithKeys(function(array $data,int $i):array{$model=PostCategory::query()->updateOrCreate(['slug'=>$data['slug']],$data+['sort_order'=>$i+1]);return[$data['slug']=>$model];});
        foreach([
            ['slug'=>'project-meridian-pierwszy-devlog','category'=>'aktualizacja-gry','title'=>'Project Meridian: pierwszy devlog','excerpt'=>'Jak projektujemy świat, który reaguje na decyzje gracza bez zasypywania go wskaźnikami.','body'=>"Pierwszy devlog Project Meridian skupia się na czytelności decyzji. Zamiast mnożyć systemy, projektujemy kilka zależności, które mają wyraźne konsekwencje dla świata.\n\nW kolejnych materiałach pokażemy proces budowy lokacji i tempo eksploracji.",'minutes'=>4,'days'=>2],
            ['slug'=>'lumen-fold-data-premiery','category'=>'premiera','title'=>'Lumen Fold otrzymuje okno premiery','excerpt'=>'Logiczna przygoda trafi na PC w czwartym kwartale 2026 roku.','body'=>'Lumen Fold wchodzi w ostatnią fazę produkcji. Zespół koncentruje się na dostępności, optymalizacji i jakości ostatnich poziomów.','minutes'=>2,'days'=>5],
            ['slug'=>'jak-budujemy-wspolny-design-system','category'=>'artykul-techniczny','title'=>'Jak budujemy wspólny design system dla wielu gatunków','excerpt'=>'Dlaczego interfejs platformy jest spokojny, a charakter pozostawiamy samym grom.','body'=>"Wspólny design system nie powinien konkurować z key artem. Dlatego bazujemy na ciemnym granacie, mięcie jako kolorze funkcjonalnym i fiolecie jako akcencie pomocniczym.\n\nAnimacje wspierają zmianę stanu, a nie dekorują każdy element strony.",'minutes'=>5,'days'=>8]
        ] as $data){ Post::query()->updateOrCreate(['slug'=>$data['slug']],['post_category_id'=>$categories[$data['category']]->id,'media_id'=>$heroMedia->id,'title'=>$data['title'],'excerpt'=>$data['excerpt'],'body'=>$data['body'],'author_name'=>'Zespół Game Studio','status'=>ContentStatus::Published,'published_at'=>now()->subDays($data['days']),'reading_minutes'=>$data['minutes'],'toc'=>[['id'=>'wprowadzenie','label'=>'Wprowadzenie']],'seo_title'=>$data['title'].' | Aktualności','seo_description'=>$data['excerpt']]); }
        Post::query()->updateOrCreate(['slug'=>'zaplanowany-pokaz-etapu-2'],['post_category_id'=>$categories['studio']->id,'title'=>'Zaplanowany wpis demonstracyjny','excerpt'=>'Ten wpis pokazuje mechanizm publikacji planowanej i nie jest jeszcze publiczny.','body'=>'Treść pojawi się publicznie dopiero po osiągnięciu daty publikacji.','author_name'=>'Redakcja','status'=>ContentStatus::Scheduled,'published_at'=>now()->addDays(30),'reading_minutes'=>1,'seo_title'=>'Zaplanowany wpis demonstracyjny','seo_description'=>'Demonstracja publikacji planowanej.']);
        Menu::query()->updateOrCreate(['location'=>'primary'],['name'=>'Menu główne','is_enabled'=>true,'items'=>[['label'=>'Gry','url'=>'/#worlds'],['label'=>'Aktualności','url'=>'/aktualnosci'],['label'=>'Dla deweloperów','url'=>'/wydaj-z-nami'],['label'=>'O nas','url'=>'/o-nas']]]);
        Menu::query()->updateOrCreate(['location'=>'legal'],['name'=>'Stopka prawna','is_enabled'=>true,'items'=>[['label'=>'Polityka prywatności','url'=>'/polityka-prywatnosci'],['label'=>'Regulamin','url'=>'#regulamin'],['label'=>'Cookies','url'=>'#cookies'],['label'=>'Reklamacje i zwroty','url'=>'#reklamacje']]]);
    }
}
