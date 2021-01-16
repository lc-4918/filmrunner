export interface FilmDisplay {
  id?: number;
  titre:string;
  titreVf?:string;
  annee?: number;
  duree?: number;
  description?: string;
  imageUrl?: string;

  support?: string;
  genre?: string;
  norme?: string;
  details?: string[];
  tip?: string;
  source?: string;

  pays: string[];
  subLangs: string[];

  themes?:string[];
  realisateurs: string[];
  shortfilms: string[];
}