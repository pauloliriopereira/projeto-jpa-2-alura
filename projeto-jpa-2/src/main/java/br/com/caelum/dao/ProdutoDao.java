package br.com.caelum.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Categoria;
import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		return em.createQuery("from Produto", Produto.class).getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);

		Predicate conjuncao = criteriaBuilder.conjunction();
		
		if (!nome.isEmpty()) {
			Path<String> nomePath = root.<String> get("nome");
			Predicate nomeIgual = criteriaBuilder.like(nomePath, "%" + nome + "%");
			conjuncao = criteriaBuilder.and(nomeIgual);
		}
		if (categoriaId != null) {
			Join<Produto, List<Categoria>> join = root.join("categorias");
			Path<Integer> categoriaProduto = join.get("id");
			
			conjuncao = criteriaBuilder.and(conjuncao,
					criteriaBuilder.equal(categoriaProduto, categoriaId));
		}
		if (lojaId != null) {
			Path<Loja> loja = root.<Loja> get("loja");
			Path<Integer> id = loja.<Integer> get("id");
			
			conjuncao = criteriaBuilder.and(conjuncao, 
					criteriaBuilder.equal(id, lojaId));
		}

		TypedQuery<Produto> typedQuery = em.createQuery(query.where(conjuncao));
		return typedQuery.getResultList();
	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

}
